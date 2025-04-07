package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.RequestCommentCreateDTO;
import com.mincho.herb.domain.comment.dto.RequestCommentUpdateDTO;
import com.mincho.herb.domain.comment.dto.ResponseCommentDTO;

public interface CommentService {

    void addComment(RequestCommentCreateDTO requestCommentCreateDTO, String email);
    void updateComment(RequestCommentUpdateDTO requestCommentUpdateDTO);
    void deleteComment(Long commentId);
    ResponseCommentDTO getCommentsByPostId(Long postId);
}
