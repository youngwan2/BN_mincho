package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.PostCategory;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addPost(RequestPostDTO requestPostDTO, String email) {
        // 유저 조회
        UserEntity userEntity = userRepository.findByEmail(email);

        // 카테고리 저장 및 조회
        PostCategory postCategory = PostCategory.builder()
                .category(requestPostDTO.getCategory())
                .build();

        PostCategoryEntity savedPostCategoryEntity = postCategoryRepository.findByCategory(requestPostDTO.getCategory());

        if(savedPostCategoryEntity == null) {
            PostCategoryEntity unsavedPostCategoryEntity = PostCategoryEntity.toEntity(postCategory);
            savedPostCategoryEntity = postCategoryRepository.save(unsavedPostCategoryEntity);
        }

        // 포스트 저장
        Post post = Post.builder().title(requestPostDTO.getTitle())
                        .contents(requestPostDTO.getContents())
                        .build();
        PostEntity unsavedPostEntity =  PostEntity.toEntity(post, userEntity, savedPostCategoryEntity);
        
        postRepository.save(unsavedPostEntity);

    }
}
