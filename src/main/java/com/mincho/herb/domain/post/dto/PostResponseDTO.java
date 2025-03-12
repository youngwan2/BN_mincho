package com.mincho.herb.domain.post.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PostResponseDTO {
    private List<PostDTO> posts; // 게시글 목록
}
