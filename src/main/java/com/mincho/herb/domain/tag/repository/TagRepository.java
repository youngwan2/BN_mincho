package com.mincho.herb.domain.tag.repository;

import com.mincho.herb.domain.tag.entity.TagEntity;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    List<TagEntity> findAll();
    Optional<TagEntity> findById(Long id);
    TagEntity save(TagEntity tagEntity);
    void delete(TagEntity tagEntity);
    List<TagEntity> findByKeywordAndType(String keyword, TagTypeEnum tagType);
}
