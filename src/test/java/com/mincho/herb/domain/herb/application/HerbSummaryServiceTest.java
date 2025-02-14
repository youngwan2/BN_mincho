package com.mincho.herb.domain.herb.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.application.herb.HerbServiceImpl;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HerbSummaryServiceTest {

    @Mock
    private HerbRepository herbRepository; // Repository Mock

    @InjectMocks
    private HerbServiceImpl herbService; // Service 구현체

    @Test
    void getHerbs_ShouldReturnHerbSummaries_WhenDataExists() {


        // Given
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        HerbEntity entity1 = new HerbEntity(
                1L,
                "205058",
                "Potentilla kleiniana (장미과)",
                "가락지나물",
                "蛇含(사함)",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                " ",
                " ",
                " "
        );
        HerbEntity entity2 = new HerbEntity(
                2L,
                "205059",
                "Taraxacum mongolicum (국화과)",
                "민들레",
                "蒲公英(포공영)",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                "http://contents/1124_01.jpg",
                " ",
                " ",
                " "
        );

        List<HerbEntity> entities = List.of(entity1, entity2);
        Page<HerbEntity> pageData = new PageImpl<>(entities, pageable, entities.size());

        when(herbRepository.findAllPaging(pageable)).thenReturn(pageData);

        // When
        List<Herb> result = herbService.getHerbSummary(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Potentilla kleiniana (장미과)", result.get(0).getBneNm());
        assertEquals("Taraxacum mongolicum (국화과)", result.get(1).getBneNm());
        verify(herbRepository, times(1)).findAllPaging(pageable);
    }

    @Test
    void getHerbs_ShouldThrowException_WhenNoDataExists() {
        // Given
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<HerbEntity> emptyPage = Page.empty();

        when(herbRepository.findAllPaging(pageable)).thenReturn(emptyPage);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class,
                () -> herbService.getHerbSummary(page, size));

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("조회 데이터가 없습니다.", exception.getMessage());
        verify(herbRepository, times(1)).findAllPaging(pageable);
    }
}
