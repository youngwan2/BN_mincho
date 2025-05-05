package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostEntity;

import java.util.List;

public interface PostService {
    void addPost(PostRequestDTO postRequestDTO, String email);
    void removePost(Long id, String email);
    void update(PostRequestDTO postRequestDTO, Long id, String email);
    PostResponseDTO getPostsByCondition(int page, int size, SearchConditionDTO searchConditionDTO);
    DetailPostResponseDTO getDetailPostById(Long id);
    PostEntity getPostById(Long id);


    /** 마이페이지 */
    List<MypagePostsDTO> getUserPosts(int page, int size);
}
