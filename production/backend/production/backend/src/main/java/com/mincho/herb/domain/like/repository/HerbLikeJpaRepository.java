package com.mincho.herb.domain.like.repository;

import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HerbLikeJpaRepository extends JpaRepository<HerbLikeEntity, Long> {

    @Query("SELECT COUNT(hl) FROM HerbLikeEntity hl WHERE hl.herb.id = :herbId AND hl.member.id = :memberId")
    int existsByMemberIdAndHerbId(@Param("memberId") Long memberId,@Param("herbId") Long herbId);
    void deleteByMemberIdAndHerbId(Long memberId, Long herbId);

    @Query("SELECT COUNT(hl) FROM HerbLikeEntity hl WHERE hl.herb.id = :herbId")
    int countByHerbId(@Param("herbId") Long herbId);
}
