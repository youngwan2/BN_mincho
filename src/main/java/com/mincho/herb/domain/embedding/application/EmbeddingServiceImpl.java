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


    private final JdbcClient jdbcClient;

    public EmbeddingServiceImpl(HerbRepository herbRepository,
                                VectorStore vectorStore,
                                JdbcClient jdbcClient) {
        this.herbRepository = herbRepository;
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }


    // 기존 약초 테이블의 데이터를 pgvector 로
    @Override
    public void embedAllHerbsToPgVector() {

        Integer count =jdbcClient.sql("select count(*) from herb_vector")
                .query(Integer.class)
                .single();
        log.debug("No of Records in the PG Vector Store={}", count);

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

                        log.debug("metadata{}",metadata);

                        return new Document(contents.toString(), metadata);
                }).toList();


        vectorStore.add(documents);
    }


}
