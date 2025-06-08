package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.*;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.entity.CommentMentionEntity;
import com.mincho.herb.domain.comment.repository.CommentMentionRepository;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.notification.application.NotificationService;
import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final CommentMentionRepository commentMentionRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final AuthUtils authUtils;

    // 댓글 추가
    @Override
    @Transactional
    public void addComment(CommentCreateRequestDTO commentCreateRequestDTO, String email) {

        UserEntity userEntity = userService.getUserByEmail(email);
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
                                                .user(userEntity)
                                                .post(postEntity)
                                                .parentComment(parentCommentEntity)
                                                .mentions(new ArrayList<>())
                                                .build();

        CommentEntity savedCommentEntity = commentRepository.save(unsavedCommentEntity); // 댓글 저장

        // 멘션 처리
        handleMentions(commentCreateRequestDTO, savedCommentEntity, postEntity);

        // 부모 댓글이 존재하는 경우, 해당 부모 댓글의 작성자에게 알림
        Long targetUserId = parentCommentEntity == null || parentCommentEntity.getDeleted() ? null : parentCommentEntity.getUser().getId();

        log.info("targetUserId:{}", targetUserId);

        String path = "/community/posts/"+postEntity.getId(); // 해당 댓글이 작성된 게시글 경로

        // 대댓인 경우, 대댓의 부모 댓글 작성자에게 알림
        if(targetUserId != null && !targetUserId.equals(userEntity.getId())) { // 자신의 댓글에는 알림을 보내지 않음
            notificationService.sendNotification(targetUserId, "comment", path, "당신의 댓글에 답글이 달렸습니다.");
        }
        // 대댓이 아닌 경우, 게시글 작성자에게 알림 (멘션이 없는 경우에만)
        else if (commentCreateRequestDTO.getMentionedUserIds() == null || commentCreateRequestDTO.getMentionedUserIds().isEmpty()) {
            Long postOwnerId = postEntity.getUser().getId();
            if (!postOwnerId.equals(userEntity.getId())) { // 자신의 게시글에는 알림을 보내지 않음
                notificationService.sendNotification(postOwnerId, "post", path, "당신의 게시글에 댓글이 달렸습니다.");
            }
        }
    }

    // 멘션 처리 메서드
    private void handleMentions(CommentCreateRequestDTO requestDTO, CommentEntity commentEntity, PostEntity postEntity) {
        if (requestDTO.getMentionedUserIds() != null && !requestDTO.getMentionedUserIds().isEmpty()) {
            String path = "/community/posts/" + postEntity.getId(); // 게시글 경로

            for (Long userId : requestDTO.getMentionedUserIds()) {
                try {
                    UserEntity mentionedUser = userService.getUserById(userId);

                    // 자신을 멘션한 경우는 무시
                    if (mentionedUser.getId().equals(commentEntity.getUser().getId())) {
                        continue;
                    }

                    // 멘션 엔티티 생성 및 저장
                    CommentMentionEntity mention = CommentMentionEntity.builder()
                            .comment(commentEntity)
                            .mentionedUser(mentionedUser)
                            .build();

                    commentEntity.addMention(mention);
                    commentMentionRepository.save(mention);

                    // 멘션된 사용자에게 알림 전송
                    notificationService.sendNotification(
                            userId,
                            "mention",
                            path,
                            commentEntity.getUser().getProfile().getNickname() + "님이 회원님을 언급했습니다."
                    );
                } catch (Exception e) {
                    log.error("사용자 ID {}에 대한 멘션 처리 중 오류 발생: {}", userId, e.getMessage());
                }
            }
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
        String email = authUtils.userCheck();
        UserEntity member = userService.getUserByEmailOrNull(email);
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
                    List<CommentDTO> replies =  commentRepository.findByParentCommentIdAndUserId(parentCommentId, memberId);

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

        String email = authUtils.userCheck();
        UserEntity member = userService.getUserByEmail(email);

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
