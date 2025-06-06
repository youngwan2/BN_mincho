package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbClusteringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herb-clustering")
@Slf4j
public class HerbClusteringController {

    private final HerbClusteringService herbClusteringService;


    @GetMapping()
    public ResponseEntity<?> getHerbClustering() {
        log.info("getHerbClustering called");

        return ResponseEntity.ok(herbClusteringService.getHerbClustering());
    }
}