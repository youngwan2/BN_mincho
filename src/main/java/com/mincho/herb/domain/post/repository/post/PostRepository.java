package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.DetailPostDTO;
import com.mincho.herb.domain.post.dto.PostDTO;
import com.mincho.herb.domain.post.dto.SearchConditionDTO;
import com.mincho.herb.domain.post.dto.UserPostResponseDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.page.PageInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);
    List<PostDTO> findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO, String email);
    Long countAllByConditions(SearchConditionDTO searchConditionDTO);
    Object[][] findByPostId(Long postId);
    PostEntity findById(Long postId);
    DetailPostDTO findDetailPostById(Long postId, String email);
    PostEntity findByIdAndIsDeletedFalse(Long postId);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    UserEntity findAuthorByPostIdAndEmail(Long postId, String email);
    void update(PostEntity postEntity);
    List<PostEntity> findAllByUser(UserEntity user);

    UserPostResponseDTO findAllByUserId(Long userId, Pageable pageable);

    Page<PostEntity> findByUserId(Long userId, Pageable pageable);
}

