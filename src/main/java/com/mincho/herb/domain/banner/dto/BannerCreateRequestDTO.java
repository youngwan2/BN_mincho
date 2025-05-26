package com.mincho.herb.domain.banner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        private LocalDateTime startDate;

        @NotNull(message = "종료일은 필수입니다")
        private LocalDateTime endDate;

        private Integer sortOrder;

        private Boolean isNewWindow;

        private String targetAudience;

        private String createdBy;
}
