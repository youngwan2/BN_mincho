package com.mincho.herb.domain.banner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerCreateRequestDTO {


        @NotBlank(message = "배너 제목은 필수입니다")
        private String title;

        @NotBlank(message = "카테고리는 필수입니다")
        private String category;

        @NotBlank(message = "이미지 URL은 필수입니다")
        private String imageUrl;

        private String linkUrl;

        private String description;

        @NotNull(message = "시작일은 필수입니다")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;

        @NotNull(message = "종료일은 필수입니다")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;

        private Integer sortOrder;

        private Boolean isNewWindow;

        private String targetAudience;

        private String createdBy;
}
