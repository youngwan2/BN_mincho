package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository {

    void save(PostEntity postEntity);
    PostEntity findById(Long postId);
    List<PostEntity> findAllByCategory(String category, Pageable pageable);
    Long findAuthorIdByPostIdAndEmail(Long postId, String email);
    void deleteById(Long id);
}
