package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    void addPost(PostRequestDTO postRequestDTO, String email);
    void removePost(Long id, String email);
    void update(PostRequestDTO postRequestDTO, Long id, String email);
    PostResponseDTO getPostsByCondition(int page, int size, SearchConditionDTO searchConditionDTO);
    DetailPostResponseDTO getDetailPostById(Long id);
    PostEntity getPostById(Long id);

    UserPostResponseDTO getUserPostsByUserId(Long userId, Pageable pageable);


    /** 마이페이지 */
    List<MypagePostsDTO> getUserPosts(int page, int size);

    /**
     * 인기 태그 목록을 조회합니다.
     *
     * @param limit 최대 태그 수
     * @return 태그명과 사용 횟수 목록
     */
    List<TagCountDTO> getPopularTags(int limit);
}
