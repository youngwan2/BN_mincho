package com.mincho.herb.domain.banner.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerOrderUpdateRequestDTO {

    @NotNull(message = "순서는 필수입니다")
    private Integer sortOrder;

    private String updatedBy;
}
