package com.mincho.herb.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class NoticeRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String category; // 점검, 업데이트 등

    private Boolean pinned;

    private List<String> tags;
}
