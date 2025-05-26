package com.mincho.herb.domain.banner.dto;

import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BannerSearchCriteriaDTO {
    private String title;
    private String category;
    private BannerStatusEnum status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
