package com.mincho.herb.domain.herb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HerbViews {
    private Long id;
    private Long viewCount;
    private Herb herb;

    public Long increase(Long prevViewCount){
        return this.viewCount = prevViewCount+1;
    }
}
