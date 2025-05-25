package com.mincho.herb.domain.herb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HerbAdminResponseDTO {
    private List<HerbAdminDTO> herbs;
    private Long totalCount;
}
