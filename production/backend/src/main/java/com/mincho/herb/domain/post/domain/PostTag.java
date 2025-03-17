package com.mincho.herb.domain.post.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostTag{

    private Long id;
    private String tag;
}
