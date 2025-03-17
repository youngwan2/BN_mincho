package com.mincho.herb.domain.herb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfoDTO {
    private Long page;
    private Long size;
}
