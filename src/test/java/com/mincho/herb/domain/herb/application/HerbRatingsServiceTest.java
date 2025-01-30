package com.mincho.herb.domain.herb.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.application.herbRatings.HerbRatingsServiceImpl;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbRatings.HerbRatingsRepository;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HerbRatingsServiceImplTest {

    @Mock
    private HerbRatingsRepository herbRatingsRepository;

    @Mock
    private HerbSummaryRepository herbSummaryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HerbRatingsServiceImpl herbRatingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHerbRatings_Success() {
        // Given
        HerbSummary herbSummary = HerbSummary.builder().cntntsSj("Sample Herb").build();
        HerbSummaryEntity herbSummaryEntity = HerbSummaryEntity.toEntity(herbSummary);
        HerbRatingsEntity herbRatingsEntity = HerbRatingsEntity.builder()
                .score(5)
                .herbSummary(herbSummaryEntity)
                .member(new UserEntity())
                .build();

        when(herbRatingsRepository.findAllBy(herbSummaryEntity))
                .thenReturn(List.of(herbRatingsEntity));
        // When
        List<HerbRatings> result = herbRatingsService.getHerbRatings(herbSummary);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getScore());
        verify(herbRatingsRepository, times(1)).findAllBy(herbSummaryEntity);
    }

    @Test
    void getHerbRatings_NotFound() {
        // Given
        HerbSummary herbSummary = HerbSummary.builder().cntntsSj("Sample Herb").build();
        HerbSummaryEntity herbSummaryEntity = HerbSummaryEntity.toEntity(herbSummary);

        when(herbRatingsRepository.findAllBy(herbSummaryEntity))
                .thenReturn(List.of());

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> {
            herbRatingsService.getHerbRatings(herbSummary);
        });

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("평점 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void addScore_Success() {
        // Given
        String herbName = "Sample Herb";
        String email = "user@example.com";
        HerbRatings herbRatings = HerbRatings.builder().id(5L).build();

        HerbSummaryEntity herbSummaryEntity = new HerbSummaryEntity();
        herbSummaryEntity.setId(5L);
        herbSummaryEntity.setHbdcNm("Sample Herb");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(5L);
        userEntity.setEmail("user@example.com");

        when(herbSummaryRepository.findByCntntsSj(herbName))
                .thenReturn(herbSummaryEntity);
        when(userRepository.findByEmail(email))
                .thenReturn(userEntity);

        // When
        assertDoesNotThrow(() -> herbRatingsService.addScore(herbRatings, herbName, email));

        // Then
        verify(herbSummaryRepository, times(1)).findByCntntsSj(herbName);
        verify(userRepository, times(1)).findByEmail(email);
        verify(herbRatingsRepository, times(1)).save(any(HerbRatingsEntity.class));
    }

    @Test
    void addScore_herbNotFound() {
        // Given
        String herbName = "Nonexistent Herb";
        String email = "user@example.com";
        HerbRatings herbRatings = HerbRatings.builder().id(5L).build();

        when(herbSummaryRepository.findByCntntsSj(herbName))
                .thenReturn(null);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> {
            herbRatingsService.addScore(herbRatings, herbName, email);
        });

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("존재하지 않는 약초 입니다.", exception.getMessage());
    }

    @Test
    void addScore_userNotFound() {
        // Given
        String herbName = "Sample Herb";
        String email = "nonexistent@example.com";
        HerbRatings herbRatings = HerbRatings.builder().id(5L).build();

        HerbSummaryEntity herbSummaryEntity = new HerbSummaryEntity();
        herbSummaryEntity.setId(1L);
        herbSummaryEntity.setBneNm("Sample Herb");

        when(herbSummaryRepository.findByCntntsSj(herbName))
                .thenReturn(herbSummaryEntity);
        when(userRepository.findByEmail(email))
                .thenReturn(null);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> {
            herbRatingsService.addScore(herbRatings, herbName, email);
        });

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("존재하지 않는 유저 입니다.", exception.getMessage());
    }
}
