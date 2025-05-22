package com.mincho.herb.domain.notice.repository;

import com.mincho.herb.domain.notice.dto.NoticeDTO;
import com.mincho.herb.domain.notice.dto.NoticeResponseDTO;
import com.mincho.herb.domain.notice.dto.NoticeSearchConditionDTO;
import com.mincho.herb.domain.notice.entity.NoticeEntity;
import com.mincho.herb.domain.notice.entity.QNoticeEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final JPAQueryFactory queryFactory;
    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public NoticeResponseDTO search(NoticeSearchConditionDTO condition, Pageable pageable) {

        QNoticeEntity notice = QNoticeEntity.noticeEntity;
        QUserEntity user = QUserEntity.userEntity;

        // 조건부 처리
        BooleanBuilder builder = new BooleanBuilder();

        // 검색어
        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            builder.and(notice.title.containsIgnoreCase(condition.getKeyword())
                    .or(notice.content.containsIgnoreCase(condition.getKeyword())));
        }

        // 카테고리
        if (condition.getCategory() != null) {
            builder.and(notice.category.eq(condition.getCategory()));
        }

        // 고정 유무
        if (condition.getPinned() != null) {
            builder.and(notice.pinned.eq(condition.getPinned()));
        }

        // 게시날짜(시작일)
        if (condition.getFromDate() != null) {
            builder.and(notice.publishedAt.goe(condition.getFromDate()));
        }

        // 게시날짜(종료일)
        if (condition.getToDate() != null) {
            builder.and(notice.publishedAt.loe(condition.getToDate()));
        }

        // 실제 조회
        List<NoticeDTO> notices = queryFactory
                .select(notice)
                .from(notice)
                .leftJoin(notice.admin, user)
                .where(builder.and(notice.deleted.isFalse())) // 삭제 안 된 글만
                .orderBy(notice.pinned.desc(), notice.publishedAt.desc()) // 고정글 우선, 최신순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(notice.id).list(
                                Projections.constructor(NoticeDTO.class,
                                        notice.id,
                                        notice.title,
                                        Expressions.stringTemplate("SUBSTRING({0} FROM 1 FOR 100)" ,notice.content),
                                        notice.category,
                                        notice.pinned,
                                        list(notice.tags), // Groupby.list() 는 연속되는 값을 리스트로 담아서 저장해준다.
                                        notice.publishedAt,
                                        notice.admin.profile.nickname
                                        )
                        )
                );

        // 전체 count
        Long totalCount = queryFactory
                .select(notice.count())
                .from(notice)
                .where(builder.and(notice.deleted.isFalse()))
                .fetchOne();

        return  NoticeResponseDTO.builder()
                .notices(notices)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public NoticeEntity save(NoticeEntity board) {
        return noticeJpaRepository.save(board);
    }

    @Override
    public Optional<NoticeEntity> findById(Long id) {
        return noticeJpaRepository.findById(id);
    }

}