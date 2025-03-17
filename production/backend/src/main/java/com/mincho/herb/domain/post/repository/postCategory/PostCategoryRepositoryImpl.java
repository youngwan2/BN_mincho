package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCategoryRepositoryImpl implements PostCategoryRepository{
    private final PostCategoryJpaRepository postCategoryJpaRepository;

    @Override
    public PostCategoryEntity save(PostCategoryEntity postCategoryEntity) {
        return postCategoryJpaRepository.save(postCategoryEntity);
    }

    @Override
    public PostCategoryEntity findByCategory(String category) {
        return postCategoryJpaRepository.findByCategory(category);
    }

    @Override
    public void deleteById(Long id) {
        postCategoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsCategory(String category) {
        return postCategoryJpaRepository.existsByCategory(category);
    }
}
