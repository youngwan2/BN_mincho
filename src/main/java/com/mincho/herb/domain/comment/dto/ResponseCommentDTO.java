package com.mincho.herb.domain.comment.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseCommentDTO {
    private List<CommentsDTO> comments;
    private Long totalCount;
}
