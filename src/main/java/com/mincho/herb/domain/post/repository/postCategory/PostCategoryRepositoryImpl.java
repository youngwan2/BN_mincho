package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostCategoryTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCategoryRepositoryImpl implements PostCategoryRepository{
    private final PostCategoryJpaRepository postCategoryJpaRepository;

    @Override
    public PostCategoryEntity save(PostCategoryEntity postCategoryEntity) {
        return postCategoryJpaRepository.save(postCategoryEntity);
    }

    @Override
    public PostCategoryEntity findByName(String name) {
        return postCategoryJpaRepository.findByName(name);
    }

    @Override
    public PostCategoryEntity findByType(String type) {
        return postCategoryJpaRepository.findByType(PostCategoryTypeEnum.valueOf(type));
    }

    @Override
    public List<PostCategoryEntity> findAll() {
        List<PostCategoryEntity> postCategoryEntities = postCategoryJpaRepository.findAll();
        if(postCategoryEntities.isEmpty()){
            return List.of();
        }

        return postCategoryEntities ;
    }

    @Override
    public void deleteById(Long id) {
        postCategoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsName(String name) {
        return postCategoryJpaRepository.existsByName(name);
    }
}
