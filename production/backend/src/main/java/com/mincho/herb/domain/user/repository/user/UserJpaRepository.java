package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsByEmail(String email);
    Optional<MemberEntity> findByEmail(String email);
    void deleteByEmail(String email);

    @Modifying
    @Query("UPDATE MemberEntity m SET m.password = :password WHERE m.email = :email")
    void updatePassword(@Param("password") String password, @Param("email") String email);

}
