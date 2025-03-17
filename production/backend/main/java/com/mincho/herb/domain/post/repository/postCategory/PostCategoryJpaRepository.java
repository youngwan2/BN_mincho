package com.mincho.herb.domain.post.repository.postCategory;

import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryJpaRepository extends JpaRepository<PostCategoryEntity, Long> {
    PostCategoryEntity findByCategory(String category);

    boolean existsByCategory(String category);
}
