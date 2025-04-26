package com.mincho.herb.domain.post.repository.postViews;

import com.mincho.herb.domain.post.entity.PostViewsEntity;

public interface PostViewsRepository {

    // 포스트 조회수 초기설정
    void save(PostViewsEntity postViewsEntity);

    // 포스트 조회수 수정
    int updatePostViewCount(Long newPostView, Long postId);

    // 포스트 조회수 조회
    PostViewsEntity findByPostId(Long postId);
}
