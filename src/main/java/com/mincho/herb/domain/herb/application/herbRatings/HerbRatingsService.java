package com.mincho.herb.domain.herb.application.herbRatings;

import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.domain.HerbSummary;

import java.util.List;

public interface HerbRatingsService {

    List<HerbRatings> getHerbRatings(HerbSummary herbSummary);
    void addScore(HerbRatings herbRatings, String herbName, String email);
}
