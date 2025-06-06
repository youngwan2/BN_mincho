package com.mincho.herb.domain.post.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    private Long id;
    private String nickname;
    private String profileImage;
}
