package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.embedding.application.EmbeddingService;
import com.mincho.herb.domain.embedding.dto.HerbEmbeddingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smile.clustering.KMeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HerbClusteringServiceImpl implements HerbClusteringService {
    private final EmbeddingService embeddingService;


    @Override
    public Map<Integer, List<Long>> getHerbClustering() {
        // 1. 임베딩 데이터 조회
        List<HerbEmbeddingDTO> dtos = embeddingService.getAllHerbsEmbedding();

        // 2. 임베딩 벡터만 추출
        double[][] data = dtos.stream()
                .map(dto -> dto.getEmbedding().stream().mapToDouble(Double::doubleValue).toArray())
                .toArray(double[][]::new);

        int k = 5; // 클러스터 개수(원하는 값으로 조정)
        KMeans kmeans = KMeans.fit(data, k);

        // 3. 클러스터별로 메타데이터(예: 증상) 그룹핑
        Map<Integer, List<Long>> clusterMap = new HashMap<>();
        for (int i = 0; i < kmeans.y.length; i++) {
            int cluster = kmeans.y[i];
            Long herbId = dtos.get(i).getHerbId(); // 증상 등 필요한 정보 추출
            clusterMap.computeIfAbsent(cluster, key -> new ArrayList<>()).add(herbId);
        }

        log.info("clusterMap={}", clusterMap);

        return clusterMap;
    }
}
