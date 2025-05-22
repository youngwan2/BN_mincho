package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.UserStatisticsDTO;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.mincho.herb.global.util.MathUtil.getGrowthRate;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{
    private final UserJpaRepository userJpaRepository; // 의도: jpa 를 외부에서 주입하여 JPA 의존성을 약화
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.toEntity(user)).toModel();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }


    // 일반 유저 인가?
    @Override
    public boolean existsByEmailAndProviderIsNull(String email) {
        return userJpaRepository.existsByEmailAndProviderIsNull(email);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) { // 회원탈퇴
        userJpaRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void updatePasswordByEmail(String password, String email) {
        userJpaRepository.updatePassword(password, email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 유저는 찾을 수 없습니다."));
    }

    @Override
    public UserEntity findByEmailOrNull(String email) {
        return userJpaRepository.findByEmail(email).orElse(null);
    }


    /**
     * 유저 통계 조회
     * @return UserStatisticsDTO
     */
    @Override
    public UserStatisticsDTO findUserStatics() {

        QUserEntity user = QUserEntity.userEntity;

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = now.withDayOfMonth(1); //현재 달의 첫번째 날
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1); // 이전 달의 첫번째 날

        LocalDateTime startOfCurrentMonth = firstDayOfCurrentMonth.atStartOfDay(); // 현재 달의 첫번째 날 00:00:00
        LocalDateTime startOfPreviousMonth = firstDayOfPreviousMonth.atStartOfDay(); // 이전 달의 첫번째 날 00:00:00
        LocalDateTime endOfPreviousMonth = startOfCurrentMonth.minusNanos(1); // 현재달의 시작 00:00:00 에서 00:00:01 을 뺀 값 -> 전월 말일 23:59:59.999999999

        // 1. 전체 회원 수
        Long totalCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        // 2. 이번 달 가입 회원 수
        Long currentMonthCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(user.createdAt.goe(startOfCurrentMonth))
                .fetchOne();

        // 3. 지난달 가입 회원 수
        Long previousMonthCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(user.createdAt.between(startOfPreviousMonth, endOfPreviousMonth)) // 이전 달의 시작 ~ 이전 달의 끝 => 지난달 가입 회원 수
                .fetchOne();


        // 4. 증감률 계산
        double growthRate = getGrowthRate(previousMonthCount, currentMonthCount);

        return new UserStatisticsDTO(
                totalCount,
                currentMonthCount,
                previousMonthCount,
                growthRate
        );
    }
}
