package com.mincho.herb.domain.bookmark.application;

import com.mincho.herb.common.exception.CustomHttpException;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HerbBookmarkServiceImplTest {

    @InjectMocks
    private HerbBookmarkServiceImpl favoriteHerbService;

    @Mock
    private HerbBookmarkRepository herbBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HerbRepository herbSummaryRepository;

    private MemberEntity mockUser;
    private HerbEntity mockHerbSummary;

    @BeforeEach
    void setUp() {
        mockUser = new MemberEntity();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");

        mockHerbSummary = new HerbEntity();
        mockHerbSummary.setId(1L);
        mockHerbSummary.setCntntsSj("인삼");
    }


    @Test
    void addHerb_Bookmark_Success() {
        // Given
        Long herbId = 1L;
        String email = "test@example.com";
        String herbName = "인삼";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findById(herbId)).thenReturn(mockHerbSummary);

        // When
        favoriteHerbService.addHerbBookmark(url, herbId);

        // Then
        verify(herbBookmarkRepository, times(1)).save(Mockito.any(HerbBookmarkEntity.class));
    }

    @Test
    void addHerb_Bookmark_UserNotFound() {
        // Given
        Long herbId = 1L;
        String email = "notfound@example.com";
        String herbName = "인삼";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addHerbBookmark(url, herbId))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("유저 정보를 찾을 수 없습니다.");
    }

    @Test
    void addHerb_HerbBookmarkNotFound() {
        // Given
        Long herbId = 1L;
        String email = "test@example.com";
        String herbName = "없는 약초";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findByCntntsSj(herbName)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addHerbBookmark(url, herbId))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("약초 정보를 찾을 수 없습니다.");
    }

    @Test
    void addHerb_Bookmark_InvalidUrl() {
        // Given
        Long herbId = 1L;
        String email = "test@example.com";
        String herbName = "인삼";
        String invalidUrl = "invalid-url"; // 잘못된 URL

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findByCntntsSj(herbName)).thenReturn(mockHerbSummary);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addHerbBookmark(invalidUrl, herbId))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("유효한 url 형식이 아닙니다.");
    }


    /* 관심 약초 */
    @Test
    void removeHerb_Bookmark_Success() {
        // given
        Long favoriteHerbId = 100L;
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        // when
        favoriteHerbService.removeHerbBookmark(favoriteHerbId);

        // then
        verify(herbBookmarkRepository, times(1))
                .deleteMemberIdAndHerbBookmarkId(mockUser.getId(), favoriteHerbId);
    }

    @Test
    void removeHerb_Bookmark_Failure() {
        // given
        Long favoriteHerbId = 100L;
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> favoriteHerbService.removeHerbBookmark(favoriteHerbId))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("유저 정보를 찾을 수 없습니다.");

        verify(herbBookmarkRepository, never()).deleteMemberIdAndHerbBookmarkId(anyLong(), anyLong());
    }
}
