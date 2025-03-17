package com.mincho.herb.domain.herb.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HerbRatingsRequestDTO {

    @Min(value = 0, message = "평점은 0 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 6 미만이어야 합니다.")
    private Integer score;

}
