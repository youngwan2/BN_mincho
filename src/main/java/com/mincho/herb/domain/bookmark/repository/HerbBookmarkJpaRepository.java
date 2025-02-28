package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface HerbBookmarkJpaRepository extends JpaRepository<HerbBookmarkEntity, Long> {
   Optional<Integer> deleteByMemberIdAndId(Long memberId, Long favoriteHerbId);


   @Query("SELECT hb.id, hb.url FROM HerbBookmarkEntity hb WHERE hb.member.id = :memberId AND hb.herb.id = :herbId")
   HerbBookmarkEntity findByMemberIdAndHerbId(
                                                @Param("memberId") Long memberId,
                                                @Param("herbId") Long herbId
   );

   @Query("SELECT COUNT(hb) FROM HerbBookmarkEntity hb WHERE hb.herb.id = :herbId")
   Integer countByHerbId(@Param("herbId") Long herbId);
}
