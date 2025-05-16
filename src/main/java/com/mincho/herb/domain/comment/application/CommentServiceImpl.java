package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.*;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.notification.application.NotificationService;
import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CommonUtils commonUtils;

    // 댓글 추가
    @Override
    @Transactional
    public void addComment(CommentCreateRequestDTO commentCreateRequestDTO, String email) {

        MemberEntity memberEntity = userService.getUserByEmail(email);
        PostEntity postEntity = postService.getPostById(commentCreateRequestDTO.getPostId());


        CommentEntity parentCommentEntity = null;
        if(commentCreateRequestDTO.getParentCommentId() != null){
            parentCommentEntity = commentRepository.findById(commentCreateRequestDTO.getParentCommentId());
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
                                                .contents(commentCreateRequestDTO.getContents())
                                                .deleted(false)
                                                .member(memberEntity)
                                                .post(postEntity)
                                                .parentComment(parentCommentEntity)
                                                .build();

        commentRepository.save(unsavedCommentEntity); // 댓글 저장

        // 알림 처리
        Long targetUserId = parentCommentEntity == null || parentCommentEntity.getDeleted() ? null : parentCommentEntity.getMember().getId();

        log.info("targetUserId:{}", targetUserId);

        String path = "/community/"+postEntity.getId(); // 해당 댓글이 작성된 게시글 경로

        if(targetUserId !=null){
            notificationService.sendNotification( targetUserId, "comment", path , "당신의 댓글에 답글이 달렸습니다.");

        } else {
            notificationService.sendNotification(postEntity.getMember().getId(),"post", path , "당신의 게시글에 댓글이 달렸습니다.");
        }
    }

    
    // 댓글 수정
    @Override
    @Transactional
    public void updateComment(CommentUpdateRequestDTO commentUpdateRequestDTO) {
      Long commentId  = commentUpdateRequestDTO.getId();
      CommentEntity commentEntity = commentRepository.findById(commentId);

      // 삭제 상태가 이미 true 라면 삭제된 댓글이라고 예외 던짐
      if(commentEntity.getDeleted()){
          throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "이미 삭제된 댓글입니다.");
      }

      commentEntity.setContents(commentUpdateRequestDTO.getContents());

      commentRepository.save(commentEntity);
    }

    // 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId);
        
        // 삭제 상태가 이미 true 라면 삭제된 댓글이라고 예외 던짐
        if(commentEntity.getDeleted()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "이미 삭제된 댓글입니다.");
        }
        
        // 삭제 상태로 변경
        commentEntity.setDeleted(true);

        commentRepository.save(commentEntity);

    }

    /* 댓글 조회*/
    @Override
    public CommentResponseDTO getCommentsByPostId(Long postId) {
        // 부모 댓글과 자식 댓글을 모두 가져오는 페치 조인 쿼리 실행
        String email = commonUtils.userCheck();
        MemberEntity member = userService.getUserByEmailOrNull(email);
        Long memberId;

        if(member != null){
            memberId = member.getId();
        } else {
            memberId = null;
        }

        List<CommentDTO> parentCommentDtos = commentRepository.findByPostIdAndMemberId(postId,memberId );

        // 댓글 목록
        List<CommentsDTO> comments = parentCommentDtos.stream().map((commentDto)-> {
                    // 부모 댓글의 ID
                    Long parentCommentId = commentDto.getId();

                    // parentId 를 가지고 있는 자식 댓글 조회
                    List<CommentDTO> replies =  commentRepository.findByParentCommentIdAndMemberId(parentCommentId, memberId);

                    // 대체 텍스트
                    String contents = "사용자에 의해 삭제된 댓글입니다.";
                    String nickname ="알 수 없는 사용자";
                    if(!commentDto.getIsDeleted()){
                        contents = commentDto.getContents();
                        nickname = commentDto.getNickname();
                    }

                    return CommentsDTO.builder()
                            .id(commentDto.getId())
                            .contents(contents)
                            .nickname(nickname)
                            .parentCommentId(commentDto.getParentCommentId())
                            .isDeleted(commentDto.getIsDeleted())
                            .isMine(commentDto.getIsMine())
                            .createdAt(commentDto.getCreatedAt())
                            .updatedAt(commentDto.getUpdatedAt())
                            .level(commentDto.getLevel())
                            .replies(replies)
                            .build();
                }
        ).toList();

        // 댓글 개수
        Long totalCount = commentRepository.countByPostId(postId);

        return CommentResponseDTO.builder()
                .comments(comments)
                .totalCount(totalCount)
                .build();

    }

    /** 마이페이지*/
    // 사용자별 댓글 조회
    @Override
    public List<MypageCommentsDTO> getMypageComments(int page, int size, String sort) {

        String email = commonUtils.userCheck();
        MemberEntity member = userService.getUserByEmail(email);

        Sort sortby = Sort.by(sort.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "id"); // 최신순 정렬
        Pageable pageable = PageRequest.of(page, size, sortby); // 최신순 페이징

        List<CommentEntity> commentEntities = commentRepository.findByMemberId(member.getId(), pageable).stream().toList();


        return  commentEntities.stream().map((commentEntity)->{
            return MypageCommentsDTO.builder()
                    .id(commentEntity.getId())
                    .contents(commentEntity.getContents())
                    .createdAt(commentEntity.getCreatedAt())
                    .build();
        }).toList();
    }

}
