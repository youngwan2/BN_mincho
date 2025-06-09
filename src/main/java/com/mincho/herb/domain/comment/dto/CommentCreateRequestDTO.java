package com.mincho.herb.domain.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreateRequestDTO {

    @Size(min = 2, max = 5000, message = "텍스트는 최소 2자 이상 5000자 이하로 작성되어야 합니다.")
    private String contents;
    private Boolean isDeleted;
    private Long postId;
    private Long parentCommentId;
    private List<Long> mentionedUserIds; // 멘션할 사용자 ID 리스트
}
