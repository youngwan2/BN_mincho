package com.mincho.herb.domain.like.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HerbLikeCountResponseDTO {

    private boolean isHerbLiked;
    private int count;
}
