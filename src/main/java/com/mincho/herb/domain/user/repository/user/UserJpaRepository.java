package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    void deleteByEmail(String email);

    @Query("UPDATE Member m SET m.password = :password WHERE m.email = :email")
    void updatePassword(@Param("password") String password, @Param("email") String email);

}
