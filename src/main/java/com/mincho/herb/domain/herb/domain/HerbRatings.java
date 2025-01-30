package com.mincho.herb.domain.herb.domain;


import com.mincho.herb.common.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class HerbRatings extends BaseEntity {
    private Long id;
    private Integer score;

    @Override
    public String toString() {
        return "HerbRatings{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
