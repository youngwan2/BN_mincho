package com.mincho.herb.domain.post.application.postView;

import com.mincho.herb.domain.post.entity.PostViewsEntity;
import com.mincho.herb.domain.post.repository.postViews.PostViewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostViewsServiceImpl implements PostViewsService{

    private final PostViewsRepository postViewsRepository;
    
    
    // 포스트 조회수 증가
    @Override
    @Transactional
    public void updateViewCount(Long postId) {
        PostViewsEntity oldPostView =postViewsRepository.findByPostId(postId);
        oldPostView.increaseViewCount();

        postViewsRepository.save(oldPostView);
    }

    // 포스트 조회수 조회
    @Override
    @Transactional(readOnly = true)
    public Long getPostViewCount(Long postId) {
        return postViewsRepository.findByPostId(postId).getViewCount();
    }
}
