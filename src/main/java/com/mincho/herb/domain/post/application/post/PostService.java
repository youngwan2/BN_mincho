package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.RequestPostDTO;

public interface PostService {
    void addPost(RequestPostDTO requestPostDTO, String email);
}
