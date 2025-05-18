package com.mincho.herb.domain.notice.repository;

import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import com.mincho.herb.domain.notice.entity.NoticeEntity;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoticeRepository {

    NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable);

    NoticeEntity save(NoticeEntity board);

    Optional<NoticeEntity> findById(Long id);
}
