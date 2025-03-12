package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository {

    void save(PostEntity postEntity);
    List<Object[]> findAllByCategoryWithLikeCount(String category, Pageable pageable);
    Object[][] findByPostId(Long postId);
    PostEntity findById(Long postId);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    MemberEntity findAuthorByPostIdAndEmail(Long postId, String email);
    void update(PostEntity postEntity);
    void deleteById(Long id);
    int countByCategory(String category);
    int countByMemberId(Long memberId);
    List<PostCountDTO> countsByCategory();
}
