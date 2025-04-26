package com.mincho.herb.domain.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MypageCommentsDTO {
    private Long id;
    private String contents;
    private LocalDateTime createdAt;

}
