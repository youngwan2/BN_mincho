package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.global.entity.TagEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HerbTagEntity Herb 와 Tag 사이의 다대다 관계를 나타내는 엔티티입니다.
 * 외래키로서 herb_id 와 tag_id 를 사용하여 Herb 와 Tag를 연결합니다.
 * 이 엔티티는 데이터베이스에서 herb_tags 테이블에 맵핑됩니다.
 */
@Entity
@Table(name="HerbTags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HerbTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "herb_id")
    private HerbEntity herb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tag;


}
