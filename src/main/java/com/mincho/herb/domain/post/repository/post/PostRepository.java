package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    void save(PostEntity postEntity);
    PostEntity findById(Long postId);
    List<PostEntity> findAll(Pageable pageable);
}
