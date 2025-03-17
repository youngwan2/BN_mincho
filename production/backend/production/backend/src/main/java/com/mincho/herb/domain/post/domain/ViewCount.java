package com.mincho.herb.domain.post.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewCount {

    private Long id;
    private Post post;
    private Long viewCount;


    public Long increase(Long oldViewCount){
        return oldViewCount+1;
    }
}
