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
public class BannerSearchRequestDTO {

    private String title;

    private String category;

    private BannerStatusEnum status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String createdBy;
}