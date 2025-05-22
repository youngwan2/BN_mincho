package com.mincho.herb.domain.user.repository;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.UserStatisticsDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserJpaRepository;
import com.mincho.herb.domain.user.repository.user.UserRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link UserRepositoryImpl}의 기능을 검증하기 위한 통합 테스트 클래스입니다.
 * <p>
 * 사용자 저장 및 통계 조회 기능이 정상적으로 동작하는지 테스트합니다.
 */
@DataJpaTest
class UserRepositoryImplTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private UserRepositoryImpl userRepository;

    /**
     * 각 테스트 실행 전에 {@link JPAQueryFactory}를 사용하여
     * {@link UserRepositoryImpl} 인스턴스를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(userJpaRepository, new JPAQueryFactory(entityManager));
    }

    /**
     * 사용자 저장 기능 테스트입니다.
     * <p>
     * 이메일 정보를 가진 {@link UserEntity}를 저장한 후,
     * 변환된 {@link User} 도메인 객체에서 이메일이 일치하는지 검증합니다.
     */
    @Test
    void save_ShouldSaveUserEntity() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setCreatedAt(LocalDateTime.now());

        // when
        User savedUser = userRepository.save(userEntity.toModel());

        // then
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    /**
     * 사용자 통계 조회 기능 테스트입니다.
     * <p>
     * 사용자 데이터가 존재할 때 {@link UserStatisticsDTO}를 통해
     * 총 사용자 수(totalCount)가 0보다 큰지 검증합니다.
     */
    @Test
    void findUserStatics_ShouldReturnUserStatistics() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setCreatedAt(LocalDateTime.now());
        userJpaRepository.save(userEntity);

        // when
        UserStatisticsDTO statistics = userRepository.findUserStatics();

        // then
        assertThat(statistics.getTotalCount()).isGreaterThan(0);
    }
}
