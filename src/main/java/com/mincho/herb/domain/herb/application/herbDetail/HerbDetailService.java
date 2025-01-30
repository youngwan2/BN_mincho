package com.mincho.herb.domain.herb.application.herbDetail;

import com.mincho.herb.domain.herb.domain.HerbDetail;

import java.io.IOException;

public interface HerbDetailService {

    void insertMany() throws IOException;

    HerbDetail getHerbDetail(String herbName);
}
