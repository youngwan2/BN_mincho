package com.mincho.herb.domain.herb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HerbResponseDTO {

    private List<HerbDTO> herbs;
    private Integer nextPage;
}
