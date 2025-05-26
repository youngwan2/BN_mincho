package com.mincho.herb.domain.user.repository;

import com.mincho.herb.domain.user.dto.AdminUserResponseDTO;
import com.mincho.herb.domain.user.dto.SortInfoDTO;
import com.mincho.herb.domain.user.dto.UserListSearchCondition;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.entity.UserStatusEnum;
import com.mincho.herb.domain.user.repository.profile.ProfileJpaRepository;

import com.mincho.herb.domain.user.repository.user.UserAdminRepositoryImpl;
import com.mincho.herb.domain.user.repository.user.UserJpaRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * UserAdminRepositoryImpl에 대한 단위 테스트 클래스입니다.
 *
 * <p>해당 테스트는 실제 데이터베이스(H2 인메모리 DB)를 사용하여,
 * UserEntity 및 ProfileEntity에 대한 저장, 조회, 수정, 삭제 동작이 올바르게 수행되는지 검증합니다.</p>
 *
 * <p>테스트 대상 메서드:</p>
 * <ul>
 *     <li>getUserInfo - 유저 검색 및 필터링 기능</li>
 *     <li>updateUserStatus - 유저 상태 변경</li>
 *     <li>deleteUser - 유저 삭제 처리</li>
 * </ul>
 */

@DataJpaTest
public class UserAdminRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(UserAdminRepositoryImplTest.class);

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ProfileJpaRepository profileJpaRepository;

    private UserAdminRepositoryImpl userAdminRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * 테스트 실행 전, 테스트에 필요한 유저와 프로필 데이터를 사전 등록합니다.
     */

    @BeforeEach
    void setUp() {
        userAdminRepository = new UserAdminRepositoryImpl(userJpaRepository, new JPAQueryFactory(entityManager));


        // 유저 1 (활성 상태)
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setStatus(UserStatusEnum.ACTIVE);
        user1.setRole("ROLE_USER");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setLastLoginAt(LocalDateTime.now());
        userJpaRepository.save(user1);

        ProfileEntity profile1 = new ProfileEntity();
        profile1.setNickname("user1");
        profile1.setIntroduction("Hello, I'm user1.");
        profile1.setAvatarUrl("http://example.com/avatar/user1.jpg");
        profile1.setUser(user1);
        profileJpaRepository.save(profile1);

        // 유저 2 (비활성 상태)
        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");
        user2.setStatus(UserStatusEnum.INACTIVE);
        user2.setRole("ROLE_USER");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setLastLoginAt(LocalDateTime.now().minusDays(1));
        userJpaRepository.save(user2);

        ProfileEntity profile2 = new ProfileEntity();
        profile2.setNickname("user2");
        profile2.setIntroduction("Hello, I'm user2.");
        profile2.setAvatarUrl("http://example.com/avatar/user2.jpg");
        profile2.setUser(user2);
        profileJpaRepository.save(profile2);
    }

    /**
     * 활성 상태의 유저를 키워드로 검색할 때, 올바른 유저가 조회되는지 검증합니다.
     * 예상: user1만 검색 결과에 포함되어야 함
     */
    @Test
    void getUserInfo_Success() {
        UserListSearchCondition condition = new UserListSearchCondition();
        condition.setKeyword("user");
        condition.setStatus("active");

        SortInfoDTO sortInfoDTO = SortInfoDTO.builder()
                .sort("createdAt")
                .order("asc")

                .build();

        Pageable pageable = PageRequest.of(0, 10);


        AdminUserResponseDTO response = userAdminRepository.getUserInfo(condition, sortInfoDTO, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getUsers()).hasSize(1);
        assertThat(response.getUsers().get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(response.getTotalCount()).isEqualTo(1);
    }

    /**
     * 잘못된 유저 상태값이 주어졌을 때 예외가 발생하는지 검증합니다.
     */
    @Test
    void getUserInfo_ThrowException_WhenStatusIsInvalid() {
        UserListSearchCondition condition = new UserListSearchCondition();
        condition.setStatus("invalid-status");

        SortInfoDTO sortInfoDTO = SortInfoDTO.builder()
                .sort("createdAt")
                .order("asc")

                .build();

        Pageable pageable = PageRequest.of(0, 10);


        assertThrows(CustomHttpException.class, () ->
                userAdminRepository.getUserInfo(condition, sortInfoDTO, pageable)
        );
    }

    /**
     * 유저 상태를 INACTIVE로 변경하고, 실제 DB에 반영되었는지 검증합니다.
     */
    @Test
    void updateUserStatus_Success() {
        UserEntity user = userJpaRepository.findAll().get(0);
        user.setStatus(UserStatusEnum.INACTIVE);

        userAdminRepository.updateUserStatus(user);

        UserEntity updatedUser = userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatusEnum.INACTIVE);
    }


    /**
     * 유저 상태를 DELETED로 변경하고, DB에서 해당 상태로 저장되었는지 검증합니다.
     */
    @Test
    void deleteUser_Success() {
        UserEntity user = userJpaRepository.findAll().get(0);
        user.setStatus(UserStatusEnum.DELETED);

        userAdminRepository.deleteUser(user);


        UserEntity deletedUser = userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(deletedUser.getStatus()).isEqualTo(UserStatusEnum.DELETED);
    }
}
