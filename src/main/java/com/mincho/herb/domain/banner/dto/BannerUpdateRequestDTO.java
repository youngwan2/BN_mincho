package com.mincho.herb.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerUpdateRequestDTO {

    private String title;

    private String category;

    private String imageUrl;

    private String linkUrl;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer sortOrder;

    private Boolean isNewWindow;

    private String targetAudience;

    private String updatedBy;
}
