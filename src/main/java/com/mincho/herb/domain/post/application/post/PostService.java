package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;

import java.util.List;

public interface PostService {
    void addPost(RequestPostDTO requestPostDTO, String email);
    void removePost(Long id, String email);
    List<ResponsePostDTO> getPostsByCategory(int page, int size, String category);
}
