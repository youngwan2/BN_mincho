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
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private MemberEntity memberEntity;
    private PostEntity postEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestPostDTO = new RequestPostDTO("Test Title", "Test Contents", "정보");

        Profile profileDomain = Profile.builder().nickname("testUser").build();
        memberEntity = MemberEntity.builder()
                .id(1L)
                .role("ROLE_USER")
                .email("test@example.com")
                .profile(ProfileEntity.toEntity(profileDomain, memberEntity))
                .build();
        postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .member(memberEntity)
                .build();

    }

    // 포스트 추가
    @Test
    void addPost_WhenCategoryExists() {
        // given
        PostCategoryEntity existingPostCategoryEntity = new PostCategoryEntity();
        existingPostCategoryEntity.setCategory("정보");

        when(userRepository.findByEmail(email)).thenReturn(memberEntity);
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
        when(userRepository.findByEmail(email)).thenReturn(memberEntity);
        when(postCategoryRepository.findByCategory(requestPostDTO.getCategory())).thenReturn(null);

        // when
        postService.addPost(requestPostDTO, email);

        // then
        verify(postCategoryRepository, times(1)).findByCategory(requestPostDTO.getCategory());
        verify(postCategoryRepository, times(1)).save(any(PostCategoryEntity.class));
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    // 게시글 조회(전체)
    @Test
    void getPostsByCategory_Success() {
        // given
        String category = "Technology";
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<Object[]> postEntities = new ArrayList<>();
        postEntities.add(new Object[]{postEntity, 5L});

        when(postRepository.findAllByCategoryWithLikeCount(category, pageable)).thenReturn(postEntities);

        // when
        List<ResponsePostDTO> result = postService.getPostsByCategory(page, size, category);


        // then
        assertEquals(1, result.size());
        assertEquals(postEntity.getId(), result.get(0).getId());
        assertEquals("Test Title", result.get(0).getTitle());
        assertEquals(5L, result.get(0).getLikeCount());
        assertEquals("testUser", result.get(0).getAuthor().getNickname());

        verify(postRepository, times(1)).findAllByCategoryWithLikeCount(category, pageable);
    }

    @Test
    void getPostsByCategory_NotFound() {
        // given
        String category = "Unknown";
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(postRepository.findAllByCategoryWithLikeCount(category, pageable)).thenReturn(List.of());

        // when
        CustomHttpException exception = assertThrows(CustomHttpException.class, () ->
                postService.getPostsByCategory(page, size, category));


        // then
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

    public PostEntity getPostEntity() {
        return postEntity;
    }

    public void setPostEntity(PostEntity postEntity) {
        this.postEntity = postEntity;
    }
}
