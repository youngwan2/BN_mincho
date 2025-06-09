package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.page.PageInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);
    PostResponseDTO findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO, String email);
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

    /**
     * 태그 사용 빈도를 집계하여 반환합니다.
     * @param limit 최대 반환 태그 수
     * @return 태그명과 사용 횟수의 리스트
     */
    List<TagCountDTO> findTagsWithCount(int limit);
}

