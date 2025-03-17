package com.mincho.herb.domain.comment.domain;

import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.user.domain.Member;
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
    private Comment parentComment;
    private Member member;
    private Post post;
    private Long level;
    private Boolean deleted;


    public static Boolean isValidCommentLevel(int level){
        return level >= 0 && level < 3;
    }


}
