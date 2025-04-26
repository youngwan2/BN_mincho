package com.mincho.herb.domain.comment.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentResponseDTO {
    private List<CommentsDTO> comments;
    private Long totalCount;
}
