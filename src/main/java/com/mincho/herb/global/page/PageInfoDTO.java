package com.mincho.herb.global.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfoDTO {
    private int page;
    private int size;
}
