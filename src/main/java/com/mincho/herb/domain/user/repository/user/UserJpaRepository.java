package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {


    boolean existsByEmail(String email);

    // 일반 유저 인가?
    boolean existsByEmailAndProviderIsNull(String email);

    Optional<UserEntity> findByEmail(String email);
    void deleteByEmail(String email);

    // 비밀번호 재설정
    @Modifying
    @Query("UPDATE UserEntity m SET m.password = :password WHERE m.email = :email")
    void updatePassword(@Param("password") String password, @Param("email") String email);


}
