package com.mincho.herb.domain.notice.application;

import com.mincho.herb.domain.notice.dto.NoticeDTO;
import com.mincho.herb.domain.notice.dto.NoticeRequestDTO;
import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import com.mincho.herb.domain.notice.entity.NoticeEntity;
import com.mincho.herb.domain.notice.repository.NoticeRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class NoticeAdminServiceImpl implements NoticeAdminService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CommonUtils commonUtils;

    /**
     * 게시글을 생성합니다.
     * 관리자 권한이 있는 사용자만 생성할 수 있습니다.
     *
     * @param dto 게시글 생성 요청 데이터
     * @throws CustomHttpException 로그인하지 않았거나 관리자 권한이 없는 경우
     */
    @Override
    @Transactional
    public void create(NoticeRequestDTO dto) {

        UserEntity userEntity = adminCheckAndReturnAdmin();

        NoticeEntity board = com.mincho.herb.domain.notice.entity.NoticeEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .tags(dto.getTags())
                .publishedAt(java.time.LocalDateTime.now())
                .admin(userEntity)
                .deleted(false)
                .build();

        noticeRepository.save(board);
    }

    /**
     * 기존 게시글을 수정합니다.
     *
     * @param id  수정할 게시글 ID
     * @param dto 수정할 데이터
     * @throws CustomHttpException 게시글이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void update(Long id, NoticeRequestDTO dto) {
        adminCheckAndReturnAdmin();

        NoticeEntity board = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setCategory(dto.getCategory());
        board.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        board.setTags(dto.getTags());

        noticeRepository.save(board);
    }

    /**
     * 게시글을 삭제(soft delete)합니다.
     *
     * @param id 삭제할 게시글 ID
     * @throws CustomHttpException 게시글이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void delete(Long id) {
        adminCheckAndReturnAdmin();

        NoticeEntity board = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        board.setDeleted(true);
        noticeRepository.save(board);
    }

    /**
     * 게시글을 조건에 따라 검색합니다. 페이징 처리 및 검색 필터(카테고리, 검색어 등)를 지원합니다.
     *
     * @param condition 검색 조건 DTO
     * @param pageable  페이징 정보
     * @return 검색된 게시글 목록 (페이지네이션 포함)
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable) {
        return noticeRepository.search(condition, pageable);
    }

    /**
     * 게시글을 ID로 조회합니다.
     *
     * @param noticeId 조회할 공지시항 ID
     * @return 검색된 게시글 상세
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeDTO getNotice(Long noticeId) {
         NoticeEntity noticeEntity = noticeRepository.findById(noticeId).orElseThrow(()->
                new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));


        return NoticeDTO.builder()
                .id(noticeEntity.getId())
                .authorName(noticeEntity.getAdmin().getProfile().getNickname())
                .tags(noticeEntity.getTags())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .category(noticeEntity.getCategory())
                .pinned(noticeEntity.getPinned())
                .publishedAt(noticeEntity.getPublishedAt())
                .build();
    }


    /** 해당 사용자가 관리자 권한이 있는지 확인합니다. */
    private UserEntity adminCheckAndReturnAdmin(){
        String email = commonUtils.userCheck();
        if (email == null) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "로그인 후 이용 가능합니다.");
        }

        UserEntity userEntity = userRepository.findByEmail(email);

        if (!userEntity.getRole().equals("ROLE_ADMIN")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자 권한이 없습니다.");
        }

        return userEntity;
    }
}

