package com.mincho.herb.domain.admin.repository.board;

import com.mincho.herb.domain.admin.dto.notice.NoticeResponseDTO;
import com.mincho.herb.domain.admin.dto.notice.NoticeSearchConditionDTO;
import com.mincho.herb.domain.admin.entity.NoticeEntity;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoticeRepository {

    NoticeResponseDTO searchBoards(NoticeSearchConditionDTO condition, Pageable pageable);

    NoticeEntity save(NoticeEntity board);

    Optional<NoticeEntity> findById(Long id);
}
