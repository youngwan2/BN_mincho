package com.mincho.herb.domain.post.application.postLike;


public interface PostLikeService {
    Boolean addPostLike(Long postId,  String email);
    Integer getPostLikeCount(Long id);
}
