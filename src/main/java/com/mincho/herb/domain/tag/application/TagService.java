package com.mincho.herb.domain.tag.application;

import com.mincho.herb.domain.tag.dto.TagDTO;
import com.mincho.herb.domain.tag.dto.TagRequestDTO;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;

import java.util.List;

public interface TagService {
    List<TagDTO> getAllTags(String keyword, TagTypeEnum tagType);
    TagDTO getTagById(Long id);
    TagDTO createTag(TagRequestDTO tagRequestDTO);
    TagDTO updateTag(Long id, TagRequestDTO tagRequestDTO);
    void deleteTag(Long id);
}
