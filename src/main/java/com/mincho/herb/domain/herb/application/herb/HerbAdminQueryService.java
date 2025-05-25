package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.dto.HerbAdminResponseDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringConditionDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import org.springframework.data.domain.Pageable;

public interface HerbAdminQueryService {
    HerbAdminResponseDTO getHerbList(String keyword, Pageable pageable, HerbFilteringConditionDTO herbFilteringConditionDTO, HerbSort herbSort);


}
