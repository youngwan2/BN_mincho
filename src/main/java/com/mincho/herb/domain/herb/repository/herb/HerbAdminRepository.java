package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface HerbAdminRepository {
    HerbEntity save(HerbEntity herbEntity);
    void saveAll(List<HerbEntity> herbs);
    void deleteById(Long id);
    HerbEntity removeHerbImagesByHerbId(Long herbId);
    List<DailyHerbStatisticsDTO> findDailyHerbStatistics(LocalDate startDate, LocalDate endDate);
    HerbStatisticsDTO findHerbStatics();
    HerbAdminResponseDTO findHerbList(String keyword, Pageable pageable, HerbFilteringConditionDTO herbFilteringConditionDTO, HerbSort herbSort);
}
