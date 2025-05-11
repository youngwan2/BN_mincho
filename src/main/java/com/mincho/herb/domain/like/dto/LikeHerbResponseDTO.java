package com.mincho.herb.domain.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeHerbResponseDTO {

    private Long herbId;
    private String herbName;
}
