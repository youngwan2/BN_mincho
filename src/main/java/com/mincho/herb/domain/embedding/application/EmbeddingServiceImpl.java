package com.mincho.herb.domain.embedding.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mincho.herb.domain.embedding.dto.HerbEmbeddingDTO;
import com.mincho.herb.domain.embedding.dto.HerbEmbeddingMetadataDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 임베딩 서비스 구현체. 약초 데이터를 벡터 형태로 변환하여 PGVector에 저장하거나 조회합니다.
 *
 * 주요 기능:
 * <ul>
 *     <li>모든 약초 데이터를 벡터화하여 PGVector 테이블에 저장</li>
 *     <li>PGVector에서 모든 임베딩 값을 조회하여 DTO로 반환</li>
 * </ul>
 */
@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final HerbRepository herbRepository;
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    /**
     * 생성자 의존성 주입
     *
     * @param herbRepository 약초 데이터 저장소
     * @param vectorStore    벡터 스토리지 (Spring AI VectorStore)
     * @param jdbcClient     PGVector에 접근할 JDBC 클라이언트
     */
    public EmbeddingServiceImpl(HerbRepository herbRepository,
                                VectorStore vectorStore,
                                JdbcClient jdbcClient) {
        this.herbRepository = herbRepository;
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    /**
     * 모든 약초 데이터를 벡터로 변환하여 PGVector 테이블에 저장합니다.
     * <p>
     * 이미 저장된 데이터가 존재할 경우 작업을 수행하지 않습니다.
     * 약초의 이름, 민간요법, 형태, 개화기, 서식지, 수확/건조 내용을 벡터화하며,
     * metadata에는 약초 ID, 학명, 본초명, 제목 정보를 포함합니다.
     *
     * @throws CustomHttpException 약초 정보가 없을 경우 예외 발생
     */
    @Override
    public void embedAllHerbsToPgVector() {
        Integer count = jdbcClient.sql("select count(*) from herb_vector")
                .query(Integer.class)
                .single();
        log.debug("No of Records in the PG Vector Store={}", count);

        if (count != 0) return;

        List<HerbEntity> herbEntities = herbRepository.findAll();

        if (herbEntities.isEmpty()) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "약초 정보가 없습니다.");
        }

        List<Document> documents = herbEntities.stream()
                .map(herbEntity -> {
                    StringBuilder contents = new StringBuilder();
                    contents.append(Optional.ofNullable(herbEntity.getCntntsSj()).orElse("")).append(" ");
                    contents.append(Optional.ofNullable(herbEntity.getPrvateTherpy()).orElse("")).append(" ");
                    contents.append(Optional.ofNullable(herbEntity.getGrowthForm()).orElse("")).append(" ");
                    contents.append(Optional.ofNullable(herbEntity.getFlowering()).orElse("")).append(" ");
                    contents.append(Optional.ofNullable(herbEntity.getHabitat()).orElse("")).append(" ");
                    contents.append(Optional.ofNullable(herbEntity.getHarvest()).orElse("")).append(" ");

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("id", String.valueOf(herbEntity.getId()));
                    metadata.put("bneNm", herbEntity.getBneNm());
                    metadata.put("hbdcNm", herbEntity.getHbdcNm());
                    metadata.put("cntntSj", herbEntity.getCntntsSj());

                    log.debug("metadata{}", metadata);

                    return new Document(contents.toString(), metadata);
                })
                .toList();

        vectorStore.add(documents);
    }

    /**
     * PGVector 테이블에 저장된 모든 임베딩 데이터를 조회하여 DTO로 반환합니다.
     *
     * @return 약초 ID와 벡터 데이터를 포함하는 HerbEmbeddingDTO 리스트
     * @throws CustomHttpException 파싱 오류 또는 예상치 못한 데이터 타입일 경우
     */
    public List<HerbEmbeddingDTO> getAllHerbsEmbedding() {
        return jdbcClient.sql("SELECT metadata, embedding FROM herb_vector")
                .query((rs, rowNum) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object embeddingObj = rs.getObject("embedding");

                    String metadataJson = rs.getString("metadata");
                    HerbEmbeddingMetadataDTO metadata;

                    try {
                        metadata = objectMapper.readValue(metadataJson, HerbEmbeddingMetadataDTO.class); // JSON 을 DTO로 변환
                    } catch (JsonProcessingException e) {
                        log.error("메타데이터 파싱 에러: {}", e);
                        throw new CustomHttpException(HttpErrorCode.INTERNAL_SERVER_ERROR, "메타데이터 파싱 에러입니다. 자세한 내용은 서버 로그를 확인해주세요.");
                    }

                    List<Double> embedding;
                    if (embeddingObj instanceof PGobject pgObj) {
                        // 벡터 문자열 파싱 예: "[0.1,0.2,0.3]" => "0.1,0.2,0.3" => [0.1, 0.2, 0.3] 형식으로 임베딩 데이터를 변환
                        String value = pgObj.getValue().replaceAll("\\[", "").replaceAll("]", "");
                        embedding = Arrays.stream(value.split(","))
                                .map(Double::parseDouble)
                                .toList();
                    } else {
                        throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "알 수 없는 임베딩 객체 타입:" + embeddingObj.getClass());
                    }

                    return new HerbEmbeddingDTO(metadata.getId(), embedding);
                })
                .list();
    }
}
