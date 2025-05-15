package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HerbSort {
    private String sort;
    private String orderBy;
}
