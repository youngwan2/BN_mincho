package com.mincho.herb.domain.herb.application.herb;

import java.util.List;
import java.util.Map;

public interface HerbClusteringService {

    Map<Integer, List<Long>> getHerbClustering();
}
