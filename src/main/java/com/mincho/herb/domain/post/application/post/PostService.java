package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponseDetailPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;
import com.mincho.herb.domain.post.entity.PostEntity;

import java.util.List;

public interface PostService {
    void addPost(RequestPostDTO requestPostDTO, String email);
    void removePost(Long id, String email);
    void update(RequestPostDTO requestPostDTO, Long id, String email);
    List<ResponsePostDTO> getPostsByCategory(int page, int size, String category);
    ResponseDetailPostDTO getDetailPostById(Long id);
    PostEntity getPostById(Long id);
}
