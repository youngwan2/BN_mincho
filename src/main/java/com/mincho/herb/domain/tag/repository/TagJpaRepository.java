package com.mincho.herb.domain.tag.repository;

import com.mincho.herb.domain.tag.entity.TagEntity;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {
    List<TagEntity> findByNameContainingIgnoreCase(String keyword);
    List<TagEntity> findByTagType(TagTypeEnum tagType);
    List<TagEntity> findByNameContainingIgnoreCaseAndTagType(String keyword, TagTypeEnum tagType);
}
