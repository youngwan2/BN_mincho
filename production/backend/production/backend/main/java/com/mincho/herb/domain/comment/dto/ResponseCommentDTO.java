package com.mincho.herb.domain.comment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCommentDTO {
    private Long id;
    private String contents;
    private String nickname;
    private Boolean isDeleted;
    private Long parentCommentId;
    private Long level;
    private List<ResponseCommentDTO> replies;
}
