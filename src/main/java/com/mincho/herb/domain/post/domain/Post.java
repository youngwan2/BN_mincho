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
    private String category;
    private User user;

    public Post withChangePostContents( String contents){
        return  Post.builder()
                .contents(contents)
                .title(this.title)
                .build();
    }
    public Post withChangePostTitle(String title){
        return  Post.builder()
                .contents(this.contents)
                .title(title)
                .build();
    }

    public Post withPost(String title, String contents){
        return  Post.builder()
                .contents(contents)
                .title(title)
                .build();
    }
}
