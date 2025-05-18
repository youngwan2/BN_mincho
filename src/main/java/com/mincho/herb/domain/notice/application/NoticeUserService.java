package com.mincho.herb.domain.notice.application;

import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import org.springframework.data.domain.Pageable;

public interface NoticeUserService {
    NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable);
}
