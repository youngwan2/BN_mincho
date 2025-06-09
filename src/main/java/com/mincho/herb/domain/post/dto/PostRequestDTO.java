package com.mincho.herb.domain.post.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    @Size(min = 2, max = 30, message = "제목은 2자 이상 30자 이하로 입력해야 합니다.")
    private String title;
    @Size(min = 10, max = 25000, message = "내용은 최소 10자 이상 25000자 이하로 입력해야 합니다.")
    private String contents;

    @Pattern(
            regexp = "DAILY|EXPERIENCE|INFO|CULTIVATION|CAUTION|EVENT|ETC",
            message = "categoryType은 DAILY, EXPERIENCE, INFO, CULTIVATION, CAUTION, EVENT, ETC 중 하나여야 합니다."
    )
    private String categoryType;

    private List<String> tags; // 태그 목록
}
