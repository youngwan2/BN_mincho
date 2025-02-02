package com.mincho.herb.domain.post.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.application.post.PostServiceImpl;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private RequestPostDTO requestPostDTO;
    private String email;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestPostDTO = new RequestPostDTO("Test Title", "Test Contents", "정보");
        email = "test@example.com";
        userEntity = new UserEntity();
        userEntity.setEmail(email);
    }

    // 포스트 추가
    @Test
    void addPost_WhenCategoryExists() {
        // given
        PostCategoryEntity existingPostCategoryEntity = new PostCategoryEntity();
        existingPostCategoryEntity.setCategory("정보");

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(postCategoryRepository.findByCategory(requestPostDTO.getCategory())).thenReturn(existingPostCategoryEntity);

        // when
        postService.addPost(requestPostDTO, email);

        // then
        verify(postCategoryRepository, times(1)).findByCategory(requestPostDTO.getCategory());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void addPost_WhenCategoryNotExists() {
        // given
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(postCategoryRepository.findByCategory(requestPostDTO.getCategory())).thenReturn(null);

        // when
        postService.addPost(requestPostDTO, email);

        // then
        verify(postCategoryRepository, times(1)).findByCategory(requestPostDTO.getCategory());
        verify(postCategoryRepository, times(1)).save(any(PostCategoryEntity.class));
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void getPostsByCategory_WhenPostsExist_ShouldReturnPostDTOs() {
        // given
        String category = "Health";
        int page = 0;
        int size = 5;

        PostEntity postEntity1 = new PostEntity(1L, "Post 1", "Content 1", null, null);
        PostEntity postEntity2 = new PostEntity(2L, "Post 2", "Content 2", null, null);
        List<PostEntity> postEntities = List.of(postEntity1, postEntity2);

        Pageable pageable = PageRequest.of(page, size);
        when(postRepository.findAllByCategory(category, pageable)).thenReturn(postEntities);

        // when
        List<ResponsePostDTO> response = postService.getPostsByCategory(page, size, category);

        // then
        assertEquals(2, response.size());
        assertEquals("Post 1", response.get(0).getTitle());
        assertEquals("Post 2", response.get(1).getTitle());
    }

    @Test
    void getPostsByCategory_WhenNoPostsExist_ShouldThrowException() {
        // given
        String category = "Health";
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);
        when(postRepository.findAllByCategory(category, pageable)).thenReturn(List.of());

        // when & then
        CustomHttpException exception = Assertions.assertThrows(CustomHttpException.class, () -> {
            postService.getPostsByCategory(page, size, category);
        });

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("해당 목록이 존재하지 않습니다.", exception.getMessage());
    }

    // 게시글 삭제
    @Test
    void removePost_Success() {
        // given
        Long postId = 1L;
        String email = "test@example.com";

        when(postRepository.findAuthorIdByPostIdAndEmail(postId, email)).thenReturn(42L);

        // when
        postService.removePost(postId, email);

        // then
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void removePost_NoPermission() {
        // given
        Long postId = 1L;
        String email = "unauthorized@example.com";

        when(postRepository.findAuthorIdByPostIdAndEmail(postId, email)).thenReturn(null);

        // when
        postService.removePost(postId, email);

        // then
        verify(postRepository, never()).deleteById(postId);
    }
}
