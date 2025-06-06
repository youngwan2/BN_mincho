package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;

import java.util.List;

public interface PostCategoryRepository {

    PostCategoryEntity save(PostCategoryEntity postCategoryEntity);
    PostCategoryEntity findByName(String name);
    PostCategoryEntity findByType(String type);
    List<PostCategoryEntity> findAll();
    void deleteById(Long id);
    boolean existsName(String name);
}
