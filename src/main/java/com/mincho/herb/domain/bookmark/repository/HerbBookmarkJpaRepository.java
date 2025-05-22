package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HerbBookmarkJpaRepository extends JpaRepository<HerbBookmarkEntity, Long> {
   int deleteByUserIdAndHerbId(Long UserId, Long herbId);

   @Query("SELECT hb FROM HerbBookmarkEntity hb WHERE hb.user.id = :userId AND hb.herb.id = :herbId")
   HerbBookmarkEntity findByUserIdAndHerbId(
                                                @Param("userId") Long userId,
                                                @Param("herbId") Long herbId
   );

   // 약초 당 북마크 개수 조회
   @Query("SELECT COUNT(hb) FROM HerbBookmarkEntity hb WHERE hb.herb.id = :herbId")
   Long countByHerbId(@Param("herbId") Long herbId);


   // 유저의 북마크 개수 조회
   @Query("SELECT COUNT(hb) FROM HerbBookmarkEntity hb WHERE hb.user.id =:userId")
   Long countByUserId(@Param("userId") Long userId);

   // 유저의 북마크 목록 조회
   @Query("SELECT hb FROM HerbBookmarkEntity hb WHERE hb.user.id =:userId")
    Optional<Page<HerbBookmarkEntity>> findByUserId(@Param("userId") Long UserId, Pageable pageable);

   // 유저 북마크 전체 삭제
   @Modifying()
   @Query("DELETE FROM HerbBookmarkEntity hb WHERE hb.user =:user")
   void deleteByUser(UserEntity user);
}
