package com.mincho.herb.domain.banner.dto;

import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponseDTO {

    private Long id;

    private String title;

    private String category;

    private String imageUrl;

    private String linkUrl;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BannerStatusEnum status;

    private String statusDescription;

    private Integer sortOrder;

    private Boolean isNewWindow;

    private String targetAudience;

    private Integer clickCount;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Boolean isCurrentlyActive;

    private Double click;
}