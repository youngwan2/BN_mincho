package com.mincho.herb.domain.herb.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HerbFilteringRequestDTO {
    private String bneNm;
    private String month;
    private String orderBy;
}
