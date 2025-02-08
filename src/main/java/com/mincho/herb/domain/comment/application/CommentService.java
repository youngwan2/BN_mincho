package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.RequestCommentDTO;

public interface CommentService {

    void addComment(RequestCommentDTO requestCommentDTO, String email);
}
