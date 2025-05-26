package com.mincho.herb.domain.tag.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // 태그 이름

    @Enumerated(EnumType.STRING)
    private TagTypeEnum tagType; // 효능/부작용

    @Column(length = 50)
    private String description; // 태그 설명
}
