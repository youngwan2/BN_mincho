package com.mincho.herb.domain.post.application;

import com.mincho.herb.domain.post.application.post.PostServiceImpl;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.entity.PostCategoryEntity;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postCategory.PostCategoryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PostServiceImplTest {

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

    @Test
    void addPost_whenCategoryExists() {
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
    void addPost_whenCategoryNotExists() {
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
}
