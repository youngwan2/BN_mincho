package com.mincho.herb.domain.qna.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDTO {
    @Size(min = 5, max = 50, message = "제목은 최소 5자 이상, 최대 50자 까지 입력해야 합니다.")
    private String title;
    @Size(min = 15, max = 500, message = "상세 내용은 최소 15자 이상, 최대 500자 까지 입력해야 합니다.")
    private String content;
    private Boolean isPrivate;

    @NotNull(message = "카테고리는 필수 항목입니다.")
    private Long category;

    private List<String> tags; // 태그 목록
}

