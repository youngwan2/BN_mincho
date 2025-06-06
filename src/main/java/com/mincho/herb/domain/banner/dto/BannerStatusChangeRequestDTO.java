package com.mincho.herb.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerStatusChangeRequestDTO {

    @NotNull(message = "상태는 필수입니다")
    @Pattern(
        regexp = "ACTIVE|INACTIVE|SCHEDULED|EXPIRED",
        message = "상태는 ACTIVE, INACTIVE, SCHEDULED, EXPIRED 중 하나여야 합니다."
    )
    private String status;

    private String updatedBy;
}
