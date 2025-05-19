package com.mincho.herb.domain.qna.repository.qna;

import com.mincho.herb.domain.qna.dto.*;
import com.mincho.herb.domain.qna.entity.*;
import com.mincho.herb.domain.user.entity.QMemberEntity;
import com.mincho.herb.domain.user.entity.QProfileEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.group.GroupByList;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


/**
 * 질문(QnA) 관련 CRUD 및 QueryDSL 기반 복합 조회 기능을 제공합니다.
 *
 * <p>JPA와 QueryDSL을 함께 활용하여 복잡한 조회 조건, 조인, 그룹핑 등
 * 고급 데이터 처리를 수행합니다.</p>
 *
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class QnaRepositoryImpl implements QnaRepository {

    private final QnaJpaRepository qnaJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 질문 엔티티를 저장합니다.
     *
     * @param qnaEntity 저장할 QnaEntity 객체
     * @return 저장된 QnaEntity 객체
     */
    // 질문 생성
    @Override
    public QnaEntity save(QnaEntity qnaEntity) {
        return qnaJpaRepository.save(qnaEntity);
    }

    /**
     * ID로 질문 엔티티를 조회합니다.
     *
     * @param id 조회할 질문의 ID
     * @return 조회된 QnaEntity
     * @throws CustomHttpException 질문이 존재하지 않을 경우 예외 발생
     */
    @Override
    public QnaEntity findById(Long id){
        return qnaJpaRepository.findById(id).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 질문 내역은 없습니다."));
    }


    /**
     * ID로 질문을 조회하고, 사용자 이메일을 기반으로 isMine 여부와 함께
     * 이미지 및 답변 정보를 포함한 QnaDTO를 반환합니다.
     *
     * @param id    질문 ID
     * @param email 현재 요청자의 이메일 (비회원일 경우 null 가능)
     * @return QnaDTO 질문 DTO
     * @throws CustomHttpException 질문이 존재하지 않을 경우 예외 발생
     */
    // 질문 상세 조회
    @Override
    public QnaDTO findById(Long id, String email) {

        QQnaEntity qnaEntity = QQnaEntity.qnaEntity;
        QQnaImageEntity qQnaImageEntity = QQnaImageEntity.qnaImageEntity;
        QAnswerImageEntity qAnswerImageEntity = QAnswerImageEntity.answerImageEntity;
        QAnswerEntity qAnswerEntity = QAnswerEntity.answerEntity;

        QMemberEntity qAnswerWriter = new QMemberEntity("answerWriter");
        QProfileEntity qAnswerProfile = new QProfileEntity("answerProfile");


        String safeEmail = email != null ? email : "";

        // qna ID 에 해당하는 Qna 조회
        QnaEntity qnaEntityResult = jpaQueryFactory
                .selectFrom(qnaEntity)
                .leftJoin(qnaEntity.writer).fetchJoin()
                .where(qnaEntity.id.eq(id))
                .fetchOne();

        if(qnaEntityResult == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 질문 내용을 찾을 수 없습니다.");
        }

        // 이미지 조회(질문자)
        List<String> imageUrls = jpaQueryFactory
                .select(qQnaImageEntity.imageUrl)
                .from(qQnaImageEntity)
                .where(qQnaImageEntity.qna.eq(qnaEntityResult))
                .fetch();


        // 답변자 조회
        List<AnswerDTO> answers = jpaQueryFactory
                .selectFrom(qAnswerEntity)
                .leftJoin(qAnswerWriter).on(qAnswerWriter.eq(qAnswerEntity.writer))
                .leftJoin(qAnswerImageEntity).on(qAnswerImageEntity.answer.eq(qAnswerEntity))
                .orderBy(qAnswerEntity.createdAt.asc())
                .transform(
                        GroupBy.groupBy(qAnswerEntity.id).list(
                                Projections.constructor(AnswerDTO.class,
                                        qAnswerEntity.id,
                                        qAnswerEntity.content,
                                        qAnswerEntity.writer.profile.nickname,
                                        qAnswerEntity.isAdopted,
                                        new CaseBuilder().when(qAnswerWriter.email.eq(safeEmail)).then(true).otherwise(false),
                                        qAnswerEntity.createdAt,
                                        GroupBy.list(qAnswerImageEntity.imageUrl)
                                )
                ));

        return QnaDTO.builder()
                .id(id)
                .title(qnaEntityResult.getTitle())
                .content(qnaEntityResult.getContent())
                .isPrivate(qnaEntityResult.getIsPrivate())
                .isMine(qnaEntityResult.getWriter().getEmail().equals(safeEmail))
                .writer(qnaEntityResult.getWriter().getProfile().getNickname())
                .imageUrls(imageUrls)
                .answers(answers)
                .createdAt(qnaEntityResult.getCreatedAt())
                .view(qnaEntityResult.getView())
                .build();
    }


    /**
     * 검색 조건 및 페이징 정보를 기반으로 질문 목록을 조회합니다.
     * 각 질문은 이미지 및 답변 리스트를 포함하며, 사용자의 이메일로 본인 여부를 확인합니다.
     *
     * @param conditionDTO 검색 조건 (제목, 내용, 날짜 필터 포함)
     * @param pageable     페이징 정보
     * @param email        현재 사용자 이메일 (본인 여부 판단용)
     * @return QnaResponseDTO 전체 질문 목록과 총 개수를 포함
     */
    // 질문 전체 조회
    @Override
    public QnaResponseDTO findAll(QnaSearchConditionDTO conditionDTO, Pageable pageable, String email) {

        QQnaEntity qnaEntity = QQnaEntity.qnaEntity;
        QQnaImageEntity qQnaImageEntity = QQnaImageEntity.qnaImageEntity;
        QAnswerEntity qAnswerEntity = QAnswerEntity.answerEntity;
        QMemberEntity qAnswerWriter = new QMemberEntity("answerWriter");
        QProfileEntity qAnswerProfile = new QProfileEntity("answerProfile");

        BooleanBuilder builder = new BooleanBuilder();
        String type = conditionDTO.getSearchType();
        String keyword = conditionDTO.getKeyword();

        // 키워드 검색
        if(type.equals("title") && keyword != null){
            builder.and(qnaEntity.title.like("%"+keyword+"%"));
        } else if(type.equals("content") && keyword != null){
            builder.and(qnaEntity.content.like("%"+keyword+"%"));
        } else if(type.isEmpty() && keyword != null){
            builder.and(qnaEntity.title.like("%"+keyword+"%").or(qnaEntity.content.like("%"+keyword+"%")));
        }

        // 기간별 조회
        if(conditionDTO.getFromDate() !=null){
            builder.and(qnaEntity.createdAt.goe(conditionDTO.getFromDate().atStartOfDay()));
        }
        
        if (conditionDTO.getToDate() != null) {
            builder.and(qnaEntity.createdAt.loe(conditionDTO.getToDate().atTime(LocalTime.MAX)));
        }

        String safeEmail = email != null ? email : "";


        List<QnaSummaryDTO> qnas = jpaQueryFactory
                .selectFrom(qnaEntity)
                .leftJoin(qAnswerEntity).on(qAnswerEntity.qna.eq(qnaEntity))
                .leftJoin(qAnswerEntity.writer, qAnswerWriter)
                .leftJoin(qAnswerWriter.profile, qAnswerProfile)
                .leftJoin(qQnaImageEntity).on(qQnaImageEntity.qna.eq(qnaEntity))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qnaEntity.createdAt.desc())
                .transform(
                        GroupBy.groupBy(qnaEntity.id).list(
                                Projections.constructor(QnaSummaryDTO.class,
                                        qnaEntity.id,
                                        qnaEntity.title,
                                        Expressions.stringTemplate("SUBSTRING({0} FROM 1 FOR 100)" ,qnaEntity.content),
                                        qnaEntity.isPrivate, // 고정글 유무
                                        new CaseBuilder().when(qnaEntity.writer.email.eq(safeEmail)).then(true).otherwise(false), // 질문자 본인확인
                                        qnaEntity.writer.profile.nickname,
                                        GroupBy.list(qQnaImageEntity.imageUrl),
                                        GroupBy.list(Projections.constructor(AnswerSummaryDTO.class,
                                                qAnswerEntity.id,
                                                qAnswerProfile.nickname, // 답변자 이름
                                                qAnswerEntity.isAdopted, // 채택 유무
                                                new CaseBuilder().when(qAnswerWriter.email.eq(safeEmail)).then(true).otherwise(false), // 답변자 본인확인
                                                qAnswerEntity.createdAt
                                        )),
                                        qnaEntity.createdAt,
                                        qnaEntity.view
                                )
                        )
                );

        Long total = jpaQueryFactory.select(qnaEntity.count()).from(qnaEntity).where(builder).fetchOne();

      return QnaResponseDTO.builder()
              .qnas(qnas)
              .totalCount(total)
              .build();
    }

    /**
     * 질문 ID로 해당 질문을 삭제합니다.
     *
     * @param qnaId 삭제할 질문 ID
     */
    @Override
    public void deleteById(Long qnaId) {
        qnaJpaRepository.deleteById(qnaId);
    }
}
