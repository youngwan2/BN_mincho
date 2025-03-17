package com.mincho.herb.domain.herb.domain;


import com.mincho.herb.common.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class HerbRatings extends BaseEntity {
    private Long id;
    private Integer score;

    // 점수 유효성 검사 메소드
    public boolean isScoreValid() {
        return score != null && score >= 0 && score <= 5;
    }

    @Override
    public String toString() {
        return "HerbRatings{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
