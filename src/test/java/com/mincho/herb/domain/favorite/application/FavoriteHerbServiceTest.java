package com.mincho.herb.domain.favorite.application;

import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.favorite.entity.FavoriteHerbEntity;
import com.mincho.herb.domain.favorite.repository.FavoriteHerbRepository;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
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
class FavoriteHerbServiceImplTest {

    @InjectMocks
    private FavoriteHerbServiceImpl favoriteHerbService;

    @Mock
    private FavoriteHerbRepository favoriteHerbRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HerbSummaryRepository herbSummaryRepository;

    private UserEntity mockUser;
    private HerbSummaryEntity mockHerbSummary;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");

        mockHerbSummary = new HerbSummaryEntity();
        mockHerbSummary.setId(1L);
        mockHerbSummary.setCntntsSj("인삼");
    }


    @Test
    void addFavoriteHerb_Success() {
        // Given
        String email = "test@example.com";
        String herbName = "인삼";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findByCntntsSj(herbName)).thenReturn(mockHerbSummary);

        // When
        favoriteHerbService.addFavoriteHerb(url, email, herbName);

        // Then
        verify(favoriteHerbRepository, times(1)).save(Mockito.any(FavoriteHerbEntity.class));
    }

    @Test
    void addFavoriteHerb_UserNotFound() {
        // Given
        String email = "notfound@example.com";
        String herbName = "인삼";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addFavoriteHerb(url, email, herbName))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("유저 정보를 찾을 수 없습니다.");
    }

    @Test
    void addFavoriteHerb_HerbNotFound() {
        // Given
        String email = "test@example.com";
        String herbName = "없는 약초";
        String url = "https://example.com";

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findByCntntsSj(herbName)).thenReturn(null);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addFavoriteHerb(url, email, herbName))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("약초 정보를 찾을 수 없습니다.");
    }

    @Test
    void addFavoriteHerb_InvalidUrl() {
        // Given
        String email = "test@example.com";
        String herbName = "인삼";
        String invalidUrl = "invalid-url"; // 잘못된 URL

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(herbSummaryRepository.findByCntntsSj(herbName)).thenReturn(mockHerbSummary);

        // Then
        assertThatThrownBy(() -> favoriteHerbService.addFavoriteHerb(invalidUrl, email, herbName))
                .isInstanceOf(CustomHttpException.class)
                .hasMessageContaining("유효한 url 형식이 아닙니다.");
    }
}
