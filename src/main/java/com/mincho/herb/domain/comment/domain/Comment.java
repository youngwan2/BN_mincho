package com.mincho.herb.domain.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private String contents;
    private Long postId;
    private Long level;
    private Boolean deleted;

    // 댓글 레벨 검증
    public static Boolean isValidCommentLevel(int level) {
        return level >= 0 && level < 3;
    }

    // 댓글 삭제 처리
    public void markAsDelete() {
        setDeleted(true);
    }
}
