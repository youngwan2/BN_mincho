package com.mincho.herb.domain.post.repository.postLike;

import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.entity.PostLikeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepository{
    private final PostLikeJpaRepository postLikeJpaRepository;
    @Override
    public void save(PostLikeEntity postLikeEntity) {
        postLikeJpaRepository.save(postLikeEntity);

    }

    @Override
    public Integer findLikeSumById(Long id) {
        return postLikeJpaRepository.findLikeCountById(id) ;
    }

    @Override
    public Boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return postLikeJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public void deleteByUserIdAndPostId(Long userId, Long postId) {
         postLikeJpaRepository.deleteByUserIdAndPostId(userId, postId);
    }
}
