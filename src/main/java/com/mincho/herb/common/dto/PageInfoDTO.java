package com.mincho.herb.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfoDTO {
    private Long page;
    private Long size;
}
