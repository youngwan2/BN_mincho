package com.mincho.herb.domain.notice.application;

import com.mincho.herb.domain.notice.dto.NoticeDTO;
import com.mincho.herb.domain.notice.dto.NoticeRequestDTO;
import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import org.springframework.data.domain.Pageable;

public interface NoticeAdminService {
    void create(NoticeRequestDTO dto);
    void update(Long id, NoticeRequestDTO dto);
    void delete(Long id);
    NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable);
    NoticeDTO getNotice(Long noticeId);
}
