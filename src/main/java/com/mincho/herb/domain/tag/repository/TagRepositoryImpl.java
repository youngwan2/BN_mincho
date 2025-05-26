package com.mincho.herb.domain.tag.repository;

import com.mincho.herb.domain.tag.entity.TagEntity;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagJpaRepository tagJpaRepository;

    @Override
    public List<TagEntity> findAll() {
        return tagJpaRepository.findAll();
    }

    @Override
    public Optional<TagEntity> findById(Long id) {
        return tagJpaRepository.findById(id);
    }

    @Override
    public TagEntity save(TagEntity tagEntity) {
        return tagJpaRepository.save(tagEntity);
    }

    @Override
    public void delete(TagEntity tagEntity) {
        tagJpaRepository.delete(tagEntity);
    }

    @Override
    public List<TagEntity> findByKeywordAndType(String keyword, TagTypeEnum tagType) {
        if (keyword == null && tagType == null) {
            return tagJpaRepository.findAll();
        } else if (keyword == null) {
            return tagJpaRepository.findByTagType(tagType);
        } else if (tagType == null) {
            return tagJpaRepository.findByNameContainingIgnoreCase(keyword);
        } else {
            return tagJpaRepository.findByNameContainingIgnoreCaseAndTagType(keyword, tagType);
        }
    }
}
