package com.mincho.herb.domain.post.application.postLike;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.PostLikeEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postLike.PostLikeRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Integer getPostLikeCount(Long id) {
        Integer count = postLikeRepository.findLikeSumById(id);
        if(count == null){
        throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"존재하지 않는 게시글 입니다.");
    }
        return count ;
    }
    @Override
    @Transactional
    public Boolean addPostLike(Long postId, String email) {
           UserEntity userEntity = userRepository.findByEmail(email);

           if(userEntity == null) {
                throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "인증된 사용자의 요청이 아닙니다.");
            }

           Long userId = userEntity.getId();
           Boolean hasLike = postLikeRepository.existsByUserIdAndPostId(userId, postId);

           if(!hasLike){
               PostEntity postEntity = postRepository.findById(postId);

               if(postEntity == null  ){
                   throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 게시글 입니다.");
               }
               PostLikeEntity unsavedPostLikeEntity = PostLikeEntity.toEntity(null, userEntity, postEntity);
               postLikeRepository.save(unsavedPostLikeEntity);

               return true;
           } else {
               postLikeRepository.deleteByUserIdAndPostId(userId, postId);

               return false;
           }
    }


}
