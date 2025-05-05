package com.mincho.herb.domain.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MypagePostResponseDTO {
    private List<MypagePostsDTO> posts;
    private Long totalCount;

}
