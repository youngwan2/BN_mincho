package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HerbBookmarkJpaRepository extends JpaRepository<HerbBookmarkEntity, Long> {
   int deleteByMemberIdAndHerbId(Long memberId, Long herbId);

   @Query("SELECT hb FROM HerbBookmarkEntity hb WHERE hb.member.id = :memberId AND hb.herb.id = :herbId")
   HerbBookmarkEntity findByMemberIdAndHerbId(
                                                @Param("memberId") Long memberId,
                                                @Param("herbId") Long herbId
   );

   // 약초 당 북마크 개수 조회
   @Query("SELECT COUNT(hb) FROM HerbBookmarkEntity hb WHERE hb.herb.id = :herbId")
   Integer countByHerbId(@Param("herbId") Long herbId);


   // 유저의 북마크 개수 조회
   @Query("SELECT COUNT(hb) FROM HerbBookmarkEntity hb WHERE hb.member.id =:memberId")
   int countByMemberId(@Param("memberId") Long memberId);

   // 유저의 북마크 목록 조회
   @Query("SELECT hb FROM HerbBookmarkEntity hb WHERE hb.member.id =:memberId")
    Optional<Page<HerbBookmarkEntity>> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
