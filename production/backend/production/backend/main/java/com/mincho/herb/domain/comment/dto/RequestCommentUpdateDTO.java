package com.mincho.herb.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCommentUpdateDTO {

    private Long id;
    private String contents;
    private Boolean isDeleted;

}
