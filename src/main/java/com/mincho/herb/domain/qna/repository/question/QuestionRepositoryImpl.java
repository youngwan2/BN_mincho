package com.mincho.herb.domain.qna.repository.question;

import com.mincho.herb.domain.qna.dto.*;
import com.mincho.herb.domain.qna.entity.*;
import com.mincho.herb.domain.user.entity.QProfileEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;


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
public class QuestionRepositoryImpl implements QuestionRepository {

    private final QuestionJpaRepository questionJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 질문 엔티티 저장합니다.
     *
     * @param questionEntity 저장할 QuestionEntity 객체
     * @return 저장된 QuestionEntity 객체
     */
    // 질문 생성
    @Override
    public QuestionEntity save(QuestionEntity questionEntity) {
        return questionJpaRepository.save(questionEntity);
    }

    /**
     * ID로 질문 엔티티를 조회합니다.
     *
     * @param id 조회할 질문의 ID
     * @return 조회된 QuestionEntity
     * @throws CustomHttpException 질문이 존재하지 않을 경우 예외 발생
     */
    @Override
    public QuestionEntity findById(Long id) {
        return questionJpaRepository.findById(id).orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 질문 내역은 없습니다."));
    }


    /**
     * ID로 질문을 조회하고, 사용자 이메일을 기반으로 isMine 여부와 함께
     * 이미지 및 답변 정보를 포함한 QnaDTO를 반환합니다.
     *
     * @param id    질문 ID
     * @param email 현재 요청자의 이메일 (비회원일 경우 null 가능)
     * @return QuestionDTO 질문 DTO
     * @throws CustomHttpException 질문이 존재하지 않을 경우 예외 발생
     */
    // 질문 상세 조회
    @Override
    public QuestionDTO findById(Long id, String email) {

        QQuestionEntity qnaEntity = QQuestionEntity.questionEntity;
        QQuestionImageEntity qQnaImageEntity = QQuestionImageEntity.questionImageEntity;
        QAnswerImageEntity qAnswerImageEntity = QAnswerImageEntity.answerImageEntity;
        QAnswerEntity qAnswerEntity = QAnswerEntity.answerEntity;

        QUserEntity qAnswerWriter = new QUserEntity("answerWriter");
        QProfileEntity qAnswerProfile = new QProfileEntity("answerProfile");


        String safeEmail = email != null ? email : "";

        log.info("QuestionRepositoryImpl.findById - id: {}, email: {}", id, safeEmail);

        // qna ID 에 해당하는 Question 조회
        QuestionEntity questionEntityResult = jpaQueryFactory
                .selectFrom(qnaEntity)
                .leftJoin(qnaEntity.writer).fetchJoin()
                .leftJoin(qnaEntity.category).fetchJoin() // 카테고리 정보도 함께 조회
                .where(qnaEntity.id.eq(id))
                .fetchOne();

        if (questionEntityResult == null) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 질문 내용을 찾을 수 없습니다.");
        }

        // 이미지 조회(질문자)
        List<String> imageUrls = jpaQueryFactory
                .select(qQnaImageEntity.imageUrl)
                .from(qQnaImageEntity)
                .where(qQnaImageEntity.question.eq(questionEntityResult))
                .fetch();


        // 답변자 조회
        List<AnswerDTO> answers = jpaQueryFactory
                .selectFrom(qAnswerEntity)
                .distinct()
                .leftJoin(qAnswerWriter).on(qAnswerWriter.eq(qAnswerEntity.writer))
                .leftJoin(qAnswerImageEntity).on(qAnswerImageEntity.answer.eq(qAnswerEntity))
                .orderBy(qAnswerEntity.createdAt.asc())
                .where(qAnswerEntity.qna.id.eq(id))
                .transform(
                        GroupBy.groupBy(qAnswerEntity.id).list(
                                Projections.constructor(AnswerDTO.class,
                                        qAnswerEntity.id,
                                        qAnswerEntity.content,
                                        qAnswerEntity.writer.id,
                                        qAnswerEntity.writer.profile.nickname,
                                        qAnswerEntity.writer.profile.avatarUrl,
                                        qAnswerEntity.isAdopted,
                                        new CaseBuilder().when(qAnswerWriter.email.eq(safeEmail)).then(true).otherwise(false),
                                        qAnswerEntity.createdAt,
                                        GroupBy.list(qAnswerImageEntity.imageUrl)
                                )
                        ));

        return QuestionDTO.builder()
                .id(id)
                .title(questionEntityResult.getTitle())
                .content(questionEntityResult.getContent())
                .isPrivate(questionEntityResult.getIsPrivate())
                .isMine(questionEntityResult.getWriter().getEmail().equals(safeEmail))
                .writerId(questionEntityResult.getWriter().getId())
                .avatarUrl(questionEntityResult.getWriter().getProfile().getAvatarUrl())
                .writer(questionEntityResult.getWriter().getProfile().getNickname())
                .imageUrls(imageUrls)
                .answers(answers)
                .tags(questionEntityResult.getTags())
                .categoryId(questionEntityResult.getCategory().getId()) // 카테고리 ID 추가
                .categoryName(questionEntityResult.getCategory().getName()) // 카테고리 이름 추가
                .createdAt(questionEntityResult.getCreatedAt())
                .view(questionEntityResult.getView())
                .build();
    }


    /**
     * 검색 조건 및 페이징 정보를 기반으로 질문 목록을 조회합니다.
     * 각 질문은 이미지 및 답변 리스트를 포함하며, 사용자의 이메일로 본인 여부를 확인합니다.
     *
     * @param conditionDTO 검색 조건 (제목, 내용, 날짜 필터 포함)
     * @param pageable     페이징 정보
     * @param email        현재 사용자 이메일 (본인 여부 판단용)
     * @return QuestionResponseDTO 전체 질문 목록과 총 개수를 포함
     */
    // 질문 전체 조회
    @Override
    public QuestionResponseDTO findAll(QuestionSearchConditionDTO conditionDTO, Pageable pageable, String email) {

        QQuestionEntity qnaEntity = QQuestionEntity.questionEntity;
        QQuestionImageEntity qQnaImageEntity = QQuestionImageEntity.questionImageEntity;
        QAnswerEntity qAnswerEntity = QAnswerEntity.answerEntity;
        QUserEntity qAnswerWriter = new QUserEntity("answerWriter");
        QProfileEntity qAnswerProfile = new QProfileEntity("answerProfile");

        BooleanBuilder builder = new BooleanBuilder();
        String type = conditionDTO.getSearchType();
        String keyword = conditionDTO.getKeyword();
        Long categoryId = conditionDTO.getCategoryId();
        String tag = conditionDTO.getTag();

        // 키워드 검색
        if(type != null){
            if(type.equals("title") && keyword != null){
                builder.and(qnaEntity.title.like("%"+keyword+"%"));
            } else if(type.equals("content") && keyword != null) {
                builder.and(qnaEntity.content.like("%" + keyword + "%"));
            } else if(type.equals("writer") && keyword != null){
                    builder.and(qnaEntity.writer.profile.nickname.like("%"+keyword+"%"));
            } else if(type.isEmpty() && keyword != null){
                builder.and(qnaEntity.title.like("%"+keyword+"%").or(qnaEntity.content.like("%"+keyword+"%")));
            }
        }

        // 카테고리 검색
        if(categoryId != null){
            builder.and(qnaEntity.category.id.eq(categoryId));
        }

        // 태그 검색 (추가된 부분)
        if(tag != null && !tag.isEmpty()) {
            builder.and(qnaEntity.tags.contains(tag));
        }

        // 기간별 조회
        if(conditionDTO.getFromDate() !=null){
            builder.and(qnaEntity.createdAt.goe(conditionDTO.getFromDate().atStartOfDay()));
        }

        if (conditionDTO.getToDate() != null) {
            builder.and(qnaEntity.createdAt.loe(conditionDTO.getToDate().atTime(LocalTime.MAX)));
        }

        String safeEmail = email != null ? email : "";


        List<QuestionSummaryDTO> qnas = jpaQueryFactory
                .selectFrom(qnaEntity)
                .leftJoin(qAnswerEntity).on(qAnswerEntity.qna.eq(qnaEntity))
                .leftJoin(qAnswerEntity.writer, qAnswerWriter)
                .leftJoin(qAnswerWriter.profile, qAnswerProfile)
                .leftJoin(qQnaImageEntity).on(qQnaImageEntity.question.eq(qnaEntity))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qnaEntity.createdAt.desc())
                .transform(
                        GroupBy.groupBy(qnaEntity.id).list(
                                Projections.constructor(QuestionSummaryDTO.class,
                                        qnaEntity.id,
                                        qnaEntity.title,
                                        Expressions.stringTemplate("SUBSTRING({0} FROM 1 FOR 100)" ,qnaEntity.content),
                                        qnaEntity.isPrivate, // 비밀글 유무
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
                                        GroupBy.list(qnaEntity.tags), // 태그 목록 추가
                                        qnaEntity.createdAt,
                                        qnaEntity.view,
                                        // Boolean 타입에 max() 함수 사용 불가능 문제 해결
                                        jpaQueryFactory.select(
                                                jpaQueryFactory.selectOne()
                                                .from(qAnswerEntity)
                                                .where(qAnswerEntity.qna.id.eq(qnaEntity.id)
                                                       .and(qAnswerEntity.isAdopted.eq(true)))
                                                .exists()
                                        )
                                )
                        )
                );

        Long total = jpaQueryFactory.select(qnaEntity.count()).from(qnaEntity).where(builder).fetchOne();

        return QuestionResponseDTO.builder()
                .qnas(qnas)
                .totalCount(total)
                .build();
    }

    /**
     * 특정 사용자가 작성한 질문 목록을 조회합니다.
     * 비공개 질문은 요청자가 작성자 본인인 경우에만 조회되고, 그렇지 않은 경우 공개 질문만 조회됩니다.
     *
     * @param userId   조회할 사용자 ID
     * @param pageable 페이징 정보
     * @param email    현재 요청자의 이메일 (본인 확인용, null 가능)
     * @return 사용자가 작성한 질문 목록과 총 개수를 포함한 응답 DTO
     */
    @Override
    public UserQuestionResponseDTO findAllByUserId(Long userId, Pageable pageable, String email) {
        QQuestionEntity qnaEntity = QQuestionEntity.questionEntity;
        QQuestionImageEntity qQnaImageEntity = QQuestionImageEntity.questionImageEntity;

        // 특정 사용자가 작성한 질문 필터링
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qnaEntity.writer.id.eq(userId));

        // 이메일이 있고 해당 사용자의 이메일과 일치하면 모든 질문(비공개 포함) 조회 가능
        // 그렇지 않으면 공개 질문만 조회
        boolean isOwner = false;
        if (email != null && !email.isEmpty()) {
            // 현재 로그인한 사용자와 조회 대상 사용자가 같은지 확인
            UserEntity userEntity = jpaQueryFactory
                    .selectFrom(QUserEntity.userEntity)
                    .where(QUserEntity.userEntity.id.eq(userId)
                            .and(QUserEntity.userEntity.email.eq(email)))
                    .fetchFirst();

            isOwner = (userEntity != null);
        }

        if (!isOwner) {
            builder.and(qnaEntity.isPrivate.eq(false)); // 공개 질문만 조회
        }

        List<UserQuestionSummaryDTO> userQuestions = jpaQueryFactory
                .select(Projections.constructor(UserQuestionSummaryDTO.class,
                        qnaEntity.id,
                        qnaEntity.title,
                        Expressions.stringTemplate("SUBSTRING({0} FROM 1 FOR 100)", qnaEntity.content),
                        qnaEntity.createdAt
                ))
                .from(qnaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qnaEntity.createdAt.desc()) // 최신순 정렬
                .fetch();

        // 총 개수 조회
        Long total = jpaQueryFactory
                .select(qnaEntity.count())
                .from(qnaEntity)
                .where(builder)
                .fetchOne();

        return UserQuestionResponseDTO.builder()
                .qnas(userQuestions)
                .totalCount(total != null ? total : 0L)
                .build();
    }

    /**
     * 질문을 ID로 삭제합니다.
     * 질문과 관련된 외래키 관계가 있는 엔티티들의 삭제 처리도 함께 고려합니다.
     * - 질문 이미지
     * - 답변 관련 데이터
     * - 태그 관계
     *
     * 참고: 실제 삭제는 서비스 계층에서 외래키 관계를 먼저 처리한 후 호출됩니다.
     *
     * @param questionId 삭제할 질문 ID
     */
    @Override
    @Transactional
    public void deleteById(Long questionId) {
        questionJpaRepository.deleteById(questionId);
    }
}
