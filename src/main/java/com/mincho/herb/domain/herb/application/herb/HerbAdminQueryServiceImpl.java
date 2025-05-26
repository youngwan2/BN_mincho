package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.dto.HerbAdminResponseDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringConditionDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import com.mincho.herb.domain.herb.repository.herb.HerbAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HerbAdminQueryServiceImpl implements HerbAdminQueryService{

    private final HerbAdminRepository herbAdminRepository;

    @Override
    public HerbAdminResponseDTO getHerbList(String keyword, Pageable pageable, HerbFilteringConditionDTO herbFilteringConditionDTO, HerbSort herbSort) {
        return herbAdminRepository.findHerbList(keyword, pageable, herbFilteringConditionDTO, herbSort);
    }
}
