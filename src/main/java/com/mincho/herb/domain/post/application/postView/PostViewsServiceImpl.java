package com.mincho.herb.domain.post.application.postView;

import com.mincho.herb.domain.post.domain.ViewCount;
import com.mincho.herb.domain.post.repository.postViews.PostViewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewsServiceImpl implements PostViewsService{

    private final PostViewsRepository postViewsRepository;
    
    
    // 포스트 조회수 증가
    @Override
    public int updateViewCount(Long postId) {
        Long oldPostView =postViewsRepository.findByPostId(postId);

        Long newPostView = ViewCount.builder().build().increase(oldPostView);

        return  postViewsRepository.updatePostViewCount(newPostView, postId);;
    }

    // 포스트 조회수 조회
    @Override
    public Long getPostView(Long postId) {
        return 0L;
    }
}
