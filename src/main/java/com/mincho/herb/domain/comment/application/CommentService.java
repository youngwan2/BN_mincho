package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.RequestCommentCreateDTO;
import com.mincho.herb.domain.comment.dto.RequestCommentUpdateDTO;
import com.mincho.herb.domain.comment.dto.ResponseCommentDTO;

import java.util.List;

public interface CommentService {

    void addComment(RequestCommentCreateDTO requestCommentCreateDTO, String email);
    List<ResponseCommentDTO> getCommentsByPostId(Long postId);
    void updateComment(RequestCommentUpdateDTO requestCommentUpdateDTO);
}
