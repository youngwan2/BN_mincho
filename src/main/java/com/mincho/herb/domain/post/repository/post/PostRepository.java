package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository {

    void save(PostEntity postEntity);
    List<Object[]> findAllByCategoryWithLikeCount(String category, Pageable pageable);
    Object[][] findDetailPostById(Long postId);
    PostEntity findById(Long postId);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    UserEntity findAuthorByPostIdAndEmail(Long postId, String email);
    void update(PostEntity postEntity);
    void deleteById(Long id);
}
