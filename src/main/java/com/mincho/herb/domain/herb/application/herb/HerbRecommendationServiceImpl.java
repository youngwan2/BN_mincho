package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.global.aop.userActivity.UserActivityAction;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HerbRecommendationServiceImpl  implements HerbRecommendationService {
    private final VectorStore vectorStore;
    private final ChatModel chatModel;


    // 유사도 기반 약초 검색
    @Override
    @UserActivityAction(action="search")
    public List<HerbDTO> getRecommendHerbs() {

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()

                .build());

        return List.of();
    }



    // RAG 기반 유사도 검색
    @Override
    @UserActivityAction(action="ai_recommendation")
    public List<RecommendHerbsDTO> getSimilaritySearchByRag(String question) {

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(15)
                .build()
        );

        if(results ==null || results.isEmpty()){
            return null;
        }

        // 리스트 형태의 포맷 변환
        BeanOutputConverter<List<RecommendHerbsDTO>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<RecommendHerbsDTO>>() {});

        // 유사도 검색 결과
        String context = results.stream().map(document -> {
            return document.getText().concat(" id: "+document.getMetadata().get("id").toString()+"\n") ;
        }).collect(Collectors.joining());

        // 포맷
        String format = converter.getFormat();

        // 프롬프트 템플릿
        String template = """
                당신은 유능한 약초 전문가입니다. 문맥에 따라 고객의 질문에 정중하게 대답해 주십시오.
                컨텍스트에 나와 있는 약초 중에서 사용자의 {question}에 등장하는 문장을 의미 기반으로 분석하고, {context} 에 등장하는 약초 목록에서 존재하는 약초를 최대 10개 내로 선택하여 포맷형태의 배열로 응답해야 합니다.
                answer 에는 왜 해당 약초를 추천하는지 명시합니다.
                만일 약초 목록에서 찾지 못했더라도, 증상과 관련해서 추가로 알아두면 도움이 되는 약초가 있으면 추천해야 합니다. 이 때 왜 추천하는지 이유도 answer에 명시해야 합니다.
                컨텍스트가 없는 경우 '현재 증상에 추천드릴 약초가 없네요. 죄송합니다.'로 시작하여, 추천하는 검색어를 answer 에 입력 후 대답하세요.
                컨텍스트가 없는 경우 id 는 -999 로 입력하여 대답하세요.
                컨텍스트가 없는 경우 herbName 은 비어두세요.
                priority 에는 우선순위로 현재 사용자에게 꼭 필요한 약초부터 1 부터 순위를 매겨주세요.
                url 은 경로 입니다. 기존적으로 '/herbs/:id' 형태입니다. 예를 들어 /herbs/148 과 같이 조회된 id 값을 입력합니다.
                
                응답은 반드시 아래 JSON 배열 형식을 따릅니다:
                {format}

                컨텍스트:
                {context}

                질문:
                {question}

                답변:
                """;

        // 프롬프트 생성
        Prompt prompt = new PromptTemplate(template, Map.of(
                "context", context,
                "question", question,
                "format", format
        )).create();

        // AI 호출
        Generation generation = chatModel.call(prompt).getResult();

        String response = generation.getOutput().getText();

        // 결과를 지정한 포맷으로 변경 후 반환
        return converter.convert(response);
    }
}
