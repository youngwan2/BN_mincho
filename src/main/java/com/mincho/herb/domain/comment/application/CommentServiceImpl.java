package com.mincho.herb.domain.comment.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.comment.dto.RequestCommentCreateDTO;
import com.mincho.herb.domain.comment.dto.RequestCommentUpdateDTO;
import com.mincho.herb.domain.comment.dto.ResponseCommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void addComment(RequestCommentCreateDTO requestCommentCreateDTO, String email) {

        MemberEntity memberEntity = userRepository.findByEmail(email);
        PostEntity postEntity = postRepository.findById(requestCommentCreateDTO.getPostId());


        CommentEntity parentCommentEntity = null;
        if(requestCommentCreateDTO.getParentCommentId() != null){
            parentCommentEntity = commentRepository.findById(requestCommentCreateDTO.getParentCommentId());
        }

        // 부모 도메인이 존재하지 않을 때 까지 level 증가
        long depth = 0L;
        CommentEntity currentComment = parentCommentEntity;

        while (currentComment != null) {
            depth++;
            currentComment = currentComment.getParentComment();
            log.info("Current depth: {}", depth);
        }

        CommentEntity unsavedCommentEntity = CommentEntity.builder()
                                                .level(depth)
                                                .contents(requestCommentCreateDTO.getContents())
                                                .deleted(false)
                                                .member(memberEntity)
                                                .post(postEntity)
                                                .parentComment(parentCommentEntity)
                                                .build();

        commentRepository.save(unsavedCommentEntity);
    }

    @Override
    public List<ResponseCommentDTO> getCommentsByPostId(Long postId) {
        // 부모 댓글과 자식 댓글을 모두 가져오는 페치 조인 쿼리 실행
        List<CommentEntity> parentCommentEntities = commentRepository.findByPostId(postId);


        return parentCommentEntities.stream().map((commentEntity)-> {
                 List<ResponseCommentDTO> replies =  commentRepository.findByParentComment(commentEntity).stream().map(replyEntity -> {
                     return ResponseCommentDTO.builder()
                             .id(replyEntity.getId())
                             .contents(replyEntity.getContents())
                             .nickname(replyEntity.getMember().getProfile().getNickname())
                             .isDeleted(replyEntity.getDeleted())
                             .level(replyEntity.getLevel())
                             .build();
                 }).toList();


                return ResponseCommentDTO.builder()
                        .id(commentEntity.getId())
                        .contents(commentEntity.getContents())
                        .nickname(commentEntity.getMember().getProfile().getNickname())
                        .isDeleted(commentEntity.getDeleted())
                        .level(commentEntity.getLevel())
                        .replies(replies)
                        .build();
                }

                ).toList();
    }
    @Override
    public void updateComment(RequestCommentUpdateDTO requestCommentUpdateDTO) {
      Long commentId  = requestCommentUpdateDTO.getId();
      CommentEntity commentEntity = commentRepository.findById(commentId);

      if(commentEntity == null){
          throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "이미 삭제된 댓글입니다.");
      }

      commentEntity.setDeleted(requestCommentUpdateDTO.getIsDeleted());
      commentEntity.setContents(requestCommentUpdateDTO.getContents());

      commentRepository.save(commentEntity);
    }
}
