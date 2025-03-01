package com.mincho.herb.domain.herbBookmark.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.bookmark.application.HerbBookmarkServiceImpl;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.bookmark.repository.HerbBookmarkRepository;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HerbBookmarkServiceImplTest {

    @InjectMocks
    private HerbBookmarkServiceImpl herbBookmarkService;

    @Mock
    private HerbBookmarkRepository herbBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HerbRepository herbRepository;

    @Mock
    private CommonUtils commonUtils;

    private MemberEntity testMember;
    private HerbEntity testHerb;

    @BeforeEach
    void setUp() {
        testMember = new MemberEntity();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");

        testHerb = new HerbEntity();
        testHerb.setId(100L);
    }

    @Test
    void addHerbBookmark_Success() {
        // Given
        String testUrl = "http://valid-url.com";
        when(commonUtils.userCheck()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);
        when(herbRepository.findById(100L)).thenReturn(testHerb);
        when(herbBookmarkRepository.findByMemberIdAndHerbId(1L, 100L)).thenReturn(null);

        // When
        assertDoesNotThrow(() -> herbBookmarkService.addHerbBookmark(testUrl, 100L));

        // Then
        verify(herbBookmarkRepository, times(1)).save(any(HerbBookmarkEntity.class));
    }

    @Test
    void addHerbBookmark_AlreadyExists_ThrowsConflictException() {
        // Given
        String testUrl = "http://valid-url.com";
        when(commonUtils.userCheck()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);
        when(herbRepository.findById(100L)).thenReturn(testHerb);
        when(herbBookmarkRepository.findByMemberIdAndHerbId(1L, 100L)).thenReturn(new HerbBookmarkEntity());

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> herbBookmarkService.addHerbBookmark(testUrl, 100L));
        assertEquals(HttpErrorCode.CONFLICT, exception.getHttpErrorCode());
    }

    @Test
    void addHerbBookmark_InvalidUrl_ThrowsBadRequestException() {
        // Given
        String invalidUrl = "invalid-url";
        when(commonUtils.userCheck()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);
        when(herbRepository.findById(100L)).thenReturn(testHerb);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> herbBookmarkService.addHerbBookmark(invalidUrl, 100L));
        assertEquals(HttpErrorCode.BAD_REQUEST, exception.getHttpErrorCode());
    }

    @Test
    void getBookmarkCount_Success() {
        // Given
        when(herbBookmarkRepository.countByHerbId(100L)).thenReturn(5);

        // When
        Integer count = herbBookmarkService.getBookmarkCount(100L);

        // Then
        assertEquals(5, count);
    }

    @Test
    void isBookmarked_WhenBookmarkExists_ReturnsTrue() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);
        when(herbBookmarkRepository.findByMemberIdAndHerbId(1L, 100L)).thenReturn(new HerbBookmarkEntity());

        // When
        Boolean isBookmarked = herbBookmarkService.isBookmarked(100L);

        // Then
        assertTrue(isBookmarked);
    }

    @Test
    void isBookmarked_WhenBookmarkDoesNotExist_ReturnsFalse() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);
        when(herbBookmarkRepository.findByMemberIdAndHerbId(1L, 100L)).thenReturn(null);

        // When
        Boolean isBookmarked = herbBookmarkService.isBookmarked(100L);

        // Then
        assertFalse(isBookmarked);
    }

    @Test
    void removeHerbBookmark_Success() {
        // Given
        when(commonUtils.userCheck()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testMember);

        // When
        assertDoesNotThrow(() -> herbBookmarkService.removeHerbBookmark(100L));

        // Then
        verify(herbBookmarkRepository, times(1)).deleteMemberIdAndHerbBookmarkId(1L, 100L);
    }

    @Test
    void removeHerbBookmark_WhenUserNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(commonUtils.userCheck()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> herbBookmarkService.removeHerbBookmark(100L));
        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
    }
}
