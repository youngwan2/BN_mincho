package com.mincho.herb.domain.like.repository;

import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HerbLikeJpaRepository extends JpaRepository<HerbLikeEntity, Long> {

    @Query("SELECT COUNT(hl) FROM HerbLikeEntity hl WHERE hl.herb.id = :herbId AND hl.user.id = :userId")
    int existsByUserIdAndHerbId(@Param("userId") Long userId,@Param("herbId") Long herbId);
    void deleteByUserIdAndHerbId(Long userId, Long herbId);

    @Query("SELECT COUNT(hl) FROM HerbLikeEntity hl WHERE hl.herb.id = :herbId")
    int countByHerbId(@Param("herbId") Long herbId);

    @Modifying
    @Query("DELETE FROM HerbLikeEntity hl WHERE hl.user =:user")
     void deleteByUser(UserEntity user);
}
