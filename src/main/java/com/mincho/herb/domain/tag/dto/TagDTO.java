package com.mincho.herb.domain.tag.dto;

import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    private Long id;
    private String name;
    private TagTypeEnum tagType;
    private String description;
}
