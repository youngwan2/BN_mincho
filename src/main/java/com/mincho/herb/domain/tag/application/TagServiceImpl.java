package com.mincho.herb.domain.tag.application;

import com.mincho.herb.domain.tag.dto.TagRequestDTO;
import com.mincho.herb.domain.tag.dto.TagDTO;
import com.mincho.herb.domain.tag.entity.TagEntity;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import com.mincho.herb.domain.tag.repository.TagRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code TagServiceImpl}는 태그(Tag)에 대한 비즈니스 로직을 처리하는 서비스 구현 클래스입니다.
 * <p>
 * 태그의 생성, 조회, 수정, 삭제 기능을 제공하며, DTO ↔ Entity 간의 변환 로직을 포함하고 있습니다.
 * {@link TagRepository}를 통해 데이터베이스와 연동됩니다.
 * </p>
 *
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final AuthUtils authUtils;

    /**
     * 키워드 및 태그 타입에 따라 필터링된 태그 목록을 조회합니다.
     *
     * @param keyword 검색 키워드
     * @param tagType 태그 타입(enum: 효능, 부작용 등)
     * @return 필터링된 태그 DTO 리스트
     */
    @Override
    public List<TagDTO> getAllTags(String keyword, TagTypeEnum tagType) {
        List<TagEntity> tags = tagRepository.findByKeywordAndType(keyword, tagType);
        return tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ID를 기준으로 단일 태그를 조회합니다.
     *
     * @param id 조회할 태그의 ID
     * @return 해당 태그의 DTO 객체
     * @throws CustomHttpException 해당 ID를 가진 태그가 존재하지 않을 경우 발생
     */
    @Override
    public TagDTO getTagById(Long id) {
        TagEntity tagEntity = tagRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 ID를 가진 태그는 없습니다.: " + id));
        return convertToDTO(tagEntity);
    }

    /**
     * 새로운 태그를 생성합니다.
     *
     * @param tagRequestDTO 생성할 태그의 요청 DTO
     * @return 생성된 태그의 DTO
     */
    @Override
    public TagDTO createTag(TagRequestDTO tagRequestDTO) {
        TagEntity tagEntity = convertToEntity(tagRequestDTO);
        TagEntity savedEntity = tagRepository.save(tagEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * 기존 태그를 수정합니다.
     *
     * @param id 수정할 태그의 ID
     * @param tagRequestDTO 수정할 태그 정보가 담긴 요청 DTO
     * @return 수정된 태그의 DTO
     * @throws CustomHttpException 해당 ID를 가진 태그가 존재하지 않을 경우 발생
     */
    @Override
    public TagDTO updateTag(Long id, TagRequestDTO tagRequestDTO) {
        TagEntity existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 ID를 가진 태그는 없습니다.: " + id));
        existingTag.setName(tagRequestDTO.getName());
        existingTag.setTagType(TagTypeEnum.valueOf(tagRequestDTO.getTagType()));
        existingTag.setDescription(tagRequestDTO.getDescription());
        TagEntity updatedEntity = tagRepository.save(existingTag);
        return convertToDTO(updatedEntity);
    }

    /**
     * 태그를 삭제합니다.
     *
     * @param id 삭제할 태그의 ID
     * @throws CustomHttpException 해당 ID를 가진 태그가 존재하지 않을 경우 발생
     */
    @Override
    public void deleteTag(Long id) {
        TagEntity tagEntity = tagRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 ID를 가진 태그는 없습니다.: " + id));
        tagRepository.delete(tagEntity);
    }

    /**
     * {@link TagEntity} 객체를 {@link TagDTO}로 변환합니다.
     *
     * @param tagEntity 변환할 엔티티 객체
     * @return DTO 객체
     */
    private TagDTO convertToDTO(TagEntity tagEntity) {
        return new TagDTO(
                tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getTagType(),
                tagEntity.getDescription()
        );
    }

    /**
     * {@link TagRequestDTO}를 {@link TagEntity}로 변환합니다.
     *
     * @param tagRequestDTO 변환할 요청 DTO
     * @return 엔티티 객체
     */
    private TagEntity convertToEntity(TagRequestDTO tagRequestDTO) {
        return TagEntity.builder()
                .name(tagRequestDTO.getName())
                .tagType(TagTypeEnum.valueOf(tagRequestDTO.getTagType()))
                .description(tagRequestDTO.getDescription())
                .build();
    }
}
