package com.mincho.herb.domain.herb.dto;

import com.mincho.herb.domain.herb.domain.Herb;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HerbResponseDTO {

    private List<Herb> herbs;
    private Integer nextPage;
}
