package com.mincho.herb.domain.post.application.postView;



public interface PostViewsService {
    // 포스트 조회수 증가
    void updateViewCount(Long postId);

    // 포스트 조회수 조회
    Long getPostViewCount(Long postId);
}
