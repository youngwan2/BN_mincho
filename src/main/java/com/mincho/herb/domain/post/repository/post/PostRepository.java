package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.dto.PostDTO;
import com.mincho.herb.domain.post.dto.PostStatisticsDTO;
import com.mincho.herb.domain.post.dto.SearchConditionDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.page.PageInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);
    List<PostDTO> findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO);
    Object[][] findByPostId(Long postId);
    PostEntity findById(Long postId);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    UserEntity findAuthorByPostIdAndEmail(Long postId, String email);
    void update(PostEntity postEntity);
    void deleteById(Long id);
    Long countByCategory(String category);
    List<PostCountDTO> countsByCategory();

    List<PostEntity> findAllByUser(UserEntity user);

    /** 마이페이지 */
    Long countByUserId(Long userId);
    Page<PostEntity> findByUserId(Long userId, Pageable pageable);

    // 게시글 통계
    PostStatisticsDTO findPostStatics();
}
