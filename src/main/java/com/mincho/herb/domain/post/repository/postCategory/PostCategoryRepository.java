package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;

public interface PostCategoryRepository {

    PostCategoryEntity save(PostCategoryEntity postCategoryEntity);
    PostCategoryEntity findByCategory(String category);
    void deleteById(Long id);
    boolean existsCategory(String category);
}
