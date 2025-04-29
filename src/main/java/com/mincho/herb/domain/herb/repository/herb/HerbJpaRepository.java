package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.PopularityHerbsDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HerbJpaRepository extends JpaRepository<HerbEntity, Long> {

    // SELECT * FROM herb WHERE
    HerbEntity findByCntntsSj(String herbName); // 약초명으로 찾기

    @Query("SELECT h FROM HerbEntity h WHERE id IN(:id1, :id2, :id3)")
    Optional<List<HerbEntity>> findRandom(
                                @Param("id1") Long id1,
                                @Param("id2") Long id2,
                                @Param("id3") Long id3
                                );


    @Query("SELECT h.id FROM HerbEntity h")
    Optional<List<Long>> findHerbIds();

    // 이달에 개화하는 약초 목록
    @Query("SELECT h FROM HerbEntity h WHERE h.flowering LIKE %:month%")
    Optional<List<HerbEntity>> findByMonth(@Param("month") String month);

    // 조회수에 따른 약초 목록
    @Query("SELECT new com.mincho.herb.domain.herb.dto.PopularityHerbsDTO(h.id, h.cntntsSj, hv.viewCount)  FROM HerbEntity h RIGHT JOIN HerbViewsEntity hv ON h.id = hv.herb.id ORDER BY hv.viewCount DESC LIMIT 10")
    List<PopularityHerbsDTO> findAllByOrderByViewCountDesc(Pageable pageable);



}
