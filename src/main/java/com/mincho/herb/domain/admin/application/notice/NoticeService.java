package com.mincho.herb.domain.admin.application.notice;

import com.mincho.herb.domain.admin.dto.notice.NoticeRequestDTO;
import com.mincho.herb.domain.admin.dto.notice.NoticeResponseDTO;
import com.mincho.herb.domain.admin.dto.notice.NoticeSearchConditionDTO;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    void create(NoticeRequestDTO dto);
    void update(Long id, NoticeRequestDTO dto);
    void delete(Long id);
    NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable);
}
