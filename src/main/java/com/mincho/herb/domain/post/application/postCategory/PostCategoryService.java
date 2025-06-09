package com.mincho.herb.domain.post.application.postCategory;

import com.mincho.herb.domain.post.dto.PostCategoryDTO;

import java.util.List;

public interface PostCategoryService {

    void getCategory(Long id);
    List<PostCategoryDTO> getPostCategories();
}
