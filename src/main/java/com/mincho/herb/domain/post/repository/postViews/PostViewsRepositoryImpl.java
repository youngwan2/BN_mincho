package com.mincho.herb.domain.post.repository.postViews;

import com.mincho.herb.domain.post.domain.ViewCount;
import com.mincho.herb.domain.post.entity.PostViewsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostViewsRepositoryImpl implements PostViewsRepository{
    
    private final PostViewsJpaRepository postViewsJpaRepository;


    // 포스트 조회수 초기 설정
    @Override
    public void save(PostViewsEntity postViewsEntity) {
        
    }

    // 포스트 조회수 증가
    @Override
    public int updatePostViewCount(Long newPostView, Long postId) {

        return  postViewsJpaRepository.updatePostViewCount(newPostView, postId);
    }

    // 포스트 조회수 조회
    @Override
    public Long findByPostId(Long postId) {
        return postViewsJpaRepository.findByPostId(postId);
    }
}
