package com.mincho.herb.domain.post.domain;

import com.mincho.herb.domain.user.domain.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Post {

    private Long id;
    private String title;
    private String contents;
    private PostCategory category;
    private Boolean isDeleted;
    private User user;
    private Boolean pined;


}
