package com.mincho.herb.domain.herb.application;


import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.application.herbSummary.HerbSummaryServiceImpl;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
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
    private HerbSummaryRepository herbSummaryRepository; // Repository Mock

    @InjectMocks
    private HerbSummaryServiceImpl herbSummaryService; // Service 구현체

    @Test
    void getHerbs_ShouldReturnHerbSummaries_WhenDataExists() {


        // Given
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        HerbSummaryEntity entity1 = new HerbSummaryEntity(
                1L,
                "205058",
                "Potentilla kleiniana (장미과)",
                "가락지나물",
                "蛇含(사함)",
                "http://contents/1124_01.jpg",
                "http://");
        HerbSummaryEntity entity2 = new HerbSummaryEntity(
                2L,
                "205059",
                "Taraxacum mongolicum (국화과)",
                "민들레",
                "蒲公英(포공영)",
                "http://contents/1124_01.jpg",
                "http://");

        List<HerbSummaryEntity> entities = List.of(entity1, entity2);
        Page<HerbSummaryEntity> pageData = new PageImpl<>(entities, pageable, entities.size());

        when(herbSummaryRepository.findAllPaging(pageable)).thenReturn(pageData);

        // When
        List<HerbSummary> result = herbSummaryService.getHerbs(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Potentilla kleiniana (장미과)", result.get(0).getBneNm());
        assertEquals("Taraxacum mongolicum (국화과)", result.get(1).getBneNm());
        verify(herbSummaryRepository, times(1)).findAllPaging(pageable);
    }

    @Test
    void getHerbs_ShouldThrowException_WhenNoDataExists() {
        // Given
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<HerbSummaryEntity> emptyPage = Page.empty();

        when(herbSummaryRepository.findAllPaging(pageable)).thenReturn(emptyPage);

        // When & Then
        CustomHttpException exception = assertThrows(CustomHttpException.class,
                () -> herbSummaryService.getHerbs(page, size));

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("조회 데이터가 없습니다.", exception.getMessage());
        verify(herbSummaryRepository, times(1)).findAllPaging(pageable);
    }

}
