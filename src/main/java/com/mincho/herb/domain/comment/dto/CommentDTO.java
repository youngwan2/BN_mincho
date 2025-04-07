package com.mincho.herb.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String contents;
    private String nickname;
    private Boolean isDeleted;
    private Long parentCommentId;
    private Long level;
    private Boolean isMine; // 이 댓글이 본인이 작성한 댓글인가?
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
