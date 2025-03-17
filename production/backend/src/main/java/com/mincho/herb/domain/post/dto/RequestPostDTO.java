package com.mincho.herb.domain.post.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestPostDTO {

    @Size(min = 2, max = 30, message = "제목은 2자 이상 30자 이하로 입력해야 합니다.")
    private String title;
    @Size(min = 10, max = 25000, message = "내용은 최소 10자 이상 25000자 이하로 입력해야 합니다.")
    private String contents;

    @Pattern(regexp = "^(정보|자유|공지)$", message = "현재는 '정보', '자유', and '공지' 카테고리만 허용됩니다.")
    private String category;
}
