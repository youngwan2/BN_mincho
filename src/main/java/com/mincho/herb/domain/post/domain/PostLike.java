package com.mincho.herb.domain.post.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostLike {
    private Long id;
    private Long postId;
    private Long userId;

}
