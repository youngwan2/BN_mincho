package com.mincho.herb.domain.tag.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagRequestDTO {

    private String name; // 태그 이름
    private String tagType; // 효능/부작용
    private String description; // 태그 설명

}
