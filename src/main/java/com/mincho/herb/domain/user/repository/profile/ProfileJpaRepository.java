package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    @Modifying
    @Query("UPDATE Profile p SET p.nickname = :nickname, p.introduction = :introduction, p.avatarUrl = :avatarUrl WHERE p.member.id = :userId")
    void updateProfile(
                          @Param("nickname") String nickname,
                          @Param("introduction") String introduction,
                          @Param("avatarUrl") String avatarUrl,
                          @Param("userId") Long userId
                          );

    @Query("SELECT m FROM Profile m WHERE m.member.id = :userId")
    ProfileEntity findProfileByUser(@Param("userId") Long userId);

}
