package com.mincho.herb.domain.post.application.postView;



public interface PostViewsService {
    // 포스트 조회수 증가
    int updateViewCount(Long postId);

    // 포스트 조회수 조회
    Long getPostView(Long postId);
}
