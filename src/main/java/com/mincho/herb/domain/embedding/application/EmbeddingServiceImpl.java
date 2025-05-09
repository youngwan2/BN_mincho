package com.mincho.herb.domain.embedding.application;

import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final HerbRepository herbRepository;
    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final JdbcClient jdbcClient;

    public EmbeddingServiceImpl(HerbRepository herbRepository,
                                VectorStore vectorStore,
                                ChatClient.Builder chatClientBuilder,
                                ChatModel chatModel,
                                JdbcClient jdbcClient) {
        this.herbRepository = herbRepository;
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.jdbcClient = jdbcClient;
    }

    // 코사인 유사도 검색
    @Override
    public List<RecommendHerbsDTO> similaritySearch(String question) {


        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(15)
                .build()
        );

        if(results ==null || results.isEmpty()){
            return null;
        }

        log.info("count:{}", results.size());


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

        log.info("format:{}", format); // 응답 스키마
        log.info("context:{}", context);
        log.info("question:{}", question);

        // 프롬프트 생성
        Prompt prompt = new PromptTemplate(template, Map.of(
                "context", context,
                "question", question,
                "format", format
        )).create();

        // AI 호출
        Generation generation = chatModel.call(prompt).getResult();

        String response = generation.getOutput().getText();
        log.info("response:{}", response );

        // 결과를 지정한 포맷으로 변경 후 반환
        return converter.convert(response);
    }

    // 기존 약초 테이블의 데이터를 pgvector 로
    @Override
    public void embedAllHerbsToPgVector() {

        Integer count =jdbcClient.sql("select count(*) from herb_vector")
                .query(Integer.class)
                .single();
        System.out.println("No of Records in the PG Vector Store="+count);

        if(count != 0) return;

        List<HerbEntity> herbEntities = herbRepository.findAll();

        if(herbEntities.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "약초 정보가 없습니다.");
        }


        List<Document> documents = herbEntities.stream()
                .map(herbEntity -> {

                        StringBuilder contents = new StringBuilder();
                        contents.append(Optional.ofNullable(herbEntity.getCntntsSj()).orElse("")).append(" "); // 약초명
                        contents.append(Optional.ofNullable(herbEntity.getPrvateTherpy()).orElse("")).append(" "); // 민간 요법
                        contents.append(Optional.ofNullable(herbEntity.getGrowthForm()).orElse("")).append(" "); // 형태
                        contents.append(Optional.ofNullable(herbEntity.getFlowering()).orElse("")).append(" "); // 개화기
                        contents.append(Optional.ofNullable(herbEntity.getHabitat()).orElse("")).append(" "); // 재배환경
                        contents.append(Optional.ofNullable(herbEntity.getHarvest()).orElse("")).append(" "); // 수확 건조

                        // 데이터 식별용 메타데이터
                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("id", String.valueOf(herbEntity.getId()));
                        metadata.put("bneNm", herbEntity.getBneNm());
                        metadata.put("hbdcNm", herbEntity.getHbdcNm());
                        metadata.put("cntntSj", herbEntity.getCntntsSj());

                        log.info("metadata{}",metadata);

                        return new Document(contents.toString(), metadata);
                }).toList();


        vectorStore.add(documents);
    }


}
