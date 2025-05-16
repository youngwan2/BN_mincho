package com.mincho.herb.domain.comment.application;

import com.mincho.herb.domain.comment.dto.CommentCreateRequestDTO;
import com.mincho.herb.domain.comment.dto.CommentResponseDTO;
import com.mincho.herb.domain.comment.dto.CommentUpdateRequestDTO;
import com.mincho.herb.domain.comment.dto.MypageCommentsDTO;

import java.util.List;

public interface CommentService {

    void addComment(CommentCreateRequestDTO commentCreateRequestDTO, String email);
    void updateComment(CommentUpdateRequestDTO commentUpdateRequestDTO);
    void deleteComment(Long commentId);
    CommentResponseDTO getCommentsByPostId(Long postId);
    List<MypageCommentsDTO> getMypageComments(int page, int size, String sort);
}
