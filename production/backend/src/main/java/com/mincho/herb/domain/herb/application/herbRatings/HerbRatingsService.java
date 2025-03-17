package com.mincho.herb.domain.herb.application.herbRatings;

import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.domain.HerbRatings;

import java.util.List;

public interface HerbRatingsService {
    List<HerbRatings> getHerbRatings(Herb herb);
    void addScore(HerbRatings herbRatings, String herbName, String email);
}
