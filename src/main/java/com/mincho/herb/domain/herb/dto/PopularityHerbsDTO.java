package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PopularityHerbsDTO {

    private Long id;
    private String herbName;
    private Long viewCount;
}
