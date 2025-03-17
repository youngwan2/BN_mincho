package com.mincho.herb.domain.post.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostImages {

    private Long id;
    private String url;
    private Post post;
}
