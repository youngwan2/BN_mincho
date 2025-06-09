package com.mincho.herb.domain.post.application.postCategory;

import com.mincho.herb.domain.post.dto.PostCategoryDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCategoryServiceImpl implements PostCategoryService{

    private final PostCategoryRepository postCategoryRepository;

    /**
     * 단일 카테고리 정보를 조회 합니다.
     * @param id 카테고리 ID
     */
    @Override
    public void getCategory(Long id) {
        
    }

    /**
     * 게시글 카테고리 목록을 조회합니다.
     * @return List<PostCategoryDTO> 게시글 카테고리 목록
     */
    @Override
    public List<PostCategoryDTO> getPostCategories() {
        return convertToDTO(postCategoryRepository.findAll());
    }

    /**
     * PostCategoryEntity 리스트를 PostCategoryDTO 리스트로 변환 합니다.
     * @param postCategoryEntities PostCategoryEntity 리스트
     * @return List<PostCategoryDTO> 변환된 PostCategoryDTO 리스트
     */
    private List<PostCategoryDTO> convertToDTO(List<PostCategoryEntity> postCategoryEntities){
        return postCategoryEntities.stream().map((category)->{
            return PostCategoryDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .type(category.getType())
                    .description(category.getDescription())
                    .build();
        }).toList();
    }
}
