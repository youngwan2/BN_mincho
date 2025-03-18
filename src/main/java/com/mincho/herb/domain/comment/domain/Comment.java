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


    public static Boolean isValidCommentLevel(int level){
        return level >= 0 && level < 3;
    }


}
