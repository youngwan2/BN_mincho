package com.mincho.herb.domain.herb.application.herbSummary;

import com.mincho.herb.domain.herb.domain.HerbSummary;

import java.io.IOException;
import java.util.List;

public interface HerbSummaryService {
    void insertMany() throws IOException;

    List<HerbSummary> getHerbs(int page, int size);

    HerbSummary getHerbByHerbName(String herbName);
}
