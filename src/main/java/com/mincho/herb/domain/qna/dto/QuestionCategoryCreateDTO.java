package com.mincho.herb.domain.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryCreateDTO {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "카테고리 이름은 2~50자 사이여야 합니다.")
    private String name;

    @Size(max = 200, message = "카테고리 설명은 200자 이하여야 합니다.")
    private String description;
}
