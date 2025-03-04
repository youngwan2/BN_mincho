package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.PostRequestDTO;
import com.mincho.herb.domain.post.dto.DetailPostResponseDTO;
import com.mincho.herb.domain.post.dto.PostResponseDTO;
import com.mincho.herb.domain.post.entity.PostEntity;

import java.util.List;

public interface PostService {
    void addPost(PostRequestDTO postRequestDTO, String email);
    void removePost(Long id, String email);
    void update(PostRequestDTO postRequestDTO, Long id, String email);
    List<PostResponseDTO> getPostsByCategory(int page, int size, String category);
    DetailPostResponseDTO getDetailPostById(Long id);
    PostEntity getPostById(Long id);
}
