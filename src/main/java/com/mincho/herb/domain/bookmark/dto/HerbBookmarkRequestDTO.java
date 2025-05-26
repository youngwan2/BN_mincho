package com.mincho.herb.domain.bookmark.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HerbBookmarkRequestDTO {

    @NotBlank(message = "URL은 필수 입력 값입니다.")
    private String url;
}
