package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.domain.Author;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponseDTO {
    private List<PostDTO> posts; // 게시글 목록
    private Long count; // 게시글 개수
}
