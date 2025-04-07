package com.mincho.herb.domain.comment.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDTO {
    private Long id;
    private String contents;
    private String nickname;
    private Boolean isDeleted;
    private Long parentCommentId;
    private Long level;
    private Boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> replies;
}
