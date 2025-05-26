package com.mincho.herb.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerListResponseDTO {

    private List<BannerResponseDTO> banners;

    private Integer totalPages;

    private Long totalElements;

    private Integer currentPage;

    private Integer pageSize;

    private Boolean hasNext;

    private Boolean hasPrevious;
}