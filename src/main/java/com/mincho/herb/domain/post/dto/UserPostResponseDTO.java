package com.mincho.herb.domain.post.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPostResponseDTO {
    private List<UserPostDTO> posts; // 게시글 목록
    private Long totalCount; // 전체 게시글 수
}
