package com.mincho.herb.domain.post.application.postLike;

import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.PostLikeEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postLike.PostLikeRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    
    // 좋아요 개수 조회
    @Override
    public Integer getPostLikeCount(Long id) {
        Integer count = postLikeRepository.findLikeSumById(id);
        if(count == null){
        throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"존재하지 않는 게시글 입니다.");
    }
        return count ;
    }
    
    // 좋아요 추가
    @Override
    @Transactional
    public Boolean addPostLike(Long postId, String email) {
           MemberEntity memberEntity = userService.getUserByEmail(email);

           if(memberEntity == null) {
                throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "인증된 사용자의 요청이 아닙니다.");
            }

           Long userId = memberEntity.getId();
           Boolean hasLike = postLikeRepository.existsByUserIdAndPostId(userId, postId);

           // 좋아요가 없으면 좋아요를 추가
           if(!hasLike){
               PostEntity postEntity = postRepository.findById(postId);

               if(postEntity == null  ){
                   throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 게시글 입니다.");
               }
               PostLikeEntity unsavedPostLikeEntity = PostLikeEntity.toEntity(null, memberEntity, postEntity);
               postLikeRepository.save(unsavedPostLikeEntity);

               return true;
               // 좋아요가 존재하면 좋아요를 제거
           } else {
               postLikeRepository.deleteByUserIdAndPostId(userId, postId);

               return false;
           }
    }
}
