package com.mincho.herb.domain.herb.dto;

import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagDTO {
    private Long id; // 태그 ID
    private String name; // 태그 이름
    private TagTypeEnum tagType; // 태그 타입 (효능, 부작용 등)
}
