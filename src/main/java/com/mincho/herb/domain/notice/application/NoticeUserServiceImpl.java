package com.mincho.herb.domain.notice.application;

import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import com.mincho.herb.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 관리자용 게시판 서비스 구현체.
 * 게시글 생성, 수정, 삭제, 조회 등의 비즈니스 로직을 처리합니다.
 *
 * <p>이 클래스는 로그인된 사용자의 인증 및 권한 확인을 수행하고,
 * 게시글에 대한 CRUD 및 조건 검색 기능을 제공합니다.</p>
 *
 * @author YoungWan Kim
 */
@Service
@RequiredArgsConstructor
public class NoticeUserServiceImpl implements NoticeUserService {

    private final NoticeRepository noticeRepository;



    /**
     * 게시글을 조건에 따라 검색합니다. 페이징 처리 및 검색 필터(카테고리, 검색어 등)를 지원합니다.
     *
     * @param condition 검색 조건 DTO
     * @param pageable  페이징 정보
     * @return 검색된 게시글 목록 (페이지네이션 포함)
     */
    @Override
    public NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable) {
        return noticeRepository.search(condition, pageable);
    }


}

