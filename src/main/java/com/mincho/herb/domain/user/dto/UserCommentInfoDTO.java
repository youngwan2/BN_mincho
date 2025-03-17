package com.mincho.herb.domain.user.dto;


import com.mincho.herb.domain.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentInfoDTO {
    private Long count;
    private List<Comment> comments;
}
