package com.mincho.herb.domain.post.repository.postLike;

import com.mincho.herb.domain.post.entity.PostLikeEntity;

public interface PostLikeRepository {

    void save(PostLikeEntity postLikeEntity);

    Integer findLikeSumById(Long id);
    Boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);
}
