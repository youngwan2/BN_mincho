package com.mincho.herb.domain.banner.dto;

import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerStatusChangeRequestDTO {

    @NotNull(message = "상태는 필수입니다")
    private BannerStatusEnum status;

    private String updatedBy;
}
