package com.mincho.herb.global.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfoDTO {
    private int page;
    private int size;
}
