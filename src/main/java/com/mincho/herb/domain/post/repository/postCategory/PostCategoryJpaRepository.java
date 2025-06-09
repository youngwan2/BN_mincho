package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostCategoryTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryJpaRepository extends JpaRepository<PostCategoryEntity, Long> {
    PostCategoryEntity findByName(String name);
    PostCategoryEntity findByType(PostCategoryTypeEnum type);
    boolean existsByName(String name);
}
