package com.mincho.herb.domain.post.application.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.domain.Author;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.PostCategory;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public void removePost(Long id, String email) {
        Long userId = postRepository.findAuthorIdByPostIdAndEmail(id, email);
        if(userId != null){
            postRepository.deleteById(id);
        }
    }

    @Override
    public List<ResponsePostDTO> getPostsByCategory(int page, int size, String category) {
         Pageable pageable = (Pageable) PageRequest.of(page, size);
         List<PostEntity> postEntities = postRepository.findAllByCategory(category, pageable);

         if(postEntities.isEmpty()){
             throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 목록이 존재하지 않습니다.");
         }

        return postEntities.stream().map(postEntity -> {
                Author author = Author.builder()
                                    .id(postEntity.getMember().getId())
                                    .nickname(postEntity.getMember().getProfile().getNickname())
                                    .build();

                return ResponsePostDTO.builder()
                    .id(postEntity.getId())
                    .category(category)
                    .title(postEntity.getTitle())
                    .contents(postEntity.getContents())
                    .author(author)
                    .createdAt(postEntity.getCreatedAt())
                    .updatedAt(postEntity.getUpdatedAt())
                    .build();
                }).toList();
    }
}
