package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.RequestCommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Override
    public void addComment(RequestCommentDTO requestCommentDTO, String email) {

        MemberEntity memberEntity = userRepository.findByEmail(email);
        PostEntity postEntity = postRepository.findById(requestCommentDTO.getPostId());


        CommentEntity parentCommentEntity = null;
        if(requestCommentDTO.getParentCommentId() != null){
            parentCommentEntity = commentRepository.findById(requestCommentDTO.getParentCommentId());
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
                                                .contents(requestCommentDTO.getContents())
                                                .deleted(false)
                                                .member(memberEntity)
                                                .post(postEntity)
                                                .parentComment(parentCommentEntity)
                                                .build();

        commentRepository.save(unsavedCommentEntity);
    }
}
