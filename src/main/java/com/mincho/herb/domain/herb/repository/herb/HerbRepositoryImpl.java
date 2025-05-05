package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import com.mincho.herb.domain.herb.dto.PopularityHerbsDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.QHerbEntity;
import com.mincho.herb.domain.herb.entity.QHerbViewsEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HerbRepositoryImpl implements HerbRepository {

    private final HerbJpaRepository herbJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void save(HerbEntity herbEntity) {
        herbJpaRepository.save(herbEntity);
    }

    @Override
    public void saveAll(List<HerbEntity> herbs) {
        herbJpaRepository.saveAll(herbs);
    }

    @Override
    public HerbEntity findByCntntsSj(String herbName) {
        return herbJpaRepository.findByCntntsSj(herbName);
    }

    @Override
    public HerbEntity findById(Long id) {
        return herbJpaRepository.findById(id).orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"해당 약초 데이터가 존재하지 않습니다."));
    }

    @Override
    public void deleteById(Long id) {
        herbJpaRepository.deleteById(id);
    }

    @Override
    public List<HerbEntity> findRandom(Long id1, Long id2, Long id3) {
        log.info("id1:{}, id2:{}, id3:{}", id2, id2, id3);
        return  herbJpaRepository.findRandom(id1, id2, id3).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 데이터 목록이 존재하지 않습니다."));
    }

    @Override
    public List<HerbEntity> findAll() {
        return herbJpaRepository.findAll();
    }

    @Override
    public List<Long> findHerbIds() {
        return herbJpaRepository.findHerbIds().orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 데이터의 id 목록이 존재하지 않습니다."));
    }

    @Override
    public List<HerbEntity> findByMonth(String month) {
        return herbJpaRepository.findByMonth(month).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 목록이 존재하지 않습니다."));
    }

    // 약초 목록 반환
    @Override
    public List<HerbDTO> findByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort, PageInfoDTO pageInfoDTO) {
        QHerbEntity herbEntity = QHerbEntity.herbEntity;
        QHerbViewsEntity herbViewsEntity = QHerbViewsEntity.herbViewsEntity;


        // == 필터링
        BooleanBuilder builder = new BooleanBuilder();

        // 페이지 시작 번호
        long offset = (long) (pageInfoDTO.getPage()) * pageInfoDTO.getSize();
        
        // 카테고리가 존재한다면,
        if(herbFilteringRequestDTO.getBneNm() != null && !herbFilteringRequestDTO.getBneNm().isEmpty()){
            builder.and(herbEntity.bneNm.contains(herbFilteringRequestDTO.getBneNm()));
        }

        // 개화기간이 존재 한다면
        if(herbFilteringRequestDTO.getMonth() !=null && !herbFilteringRequestDTO.getMonth().isEmpty()){
            builder.and(herbEntity.flowering.contains(herbFilteringRequestDTO.getMonth()));
        }

        // == 정렬
        OrderSpecifier<?> orderSpecifier = herbEntity.cntntsSj.asc(); // 기본은 이름 순으로

        if("cntntsSj".equals(herbSort.getSort())){
            orderSpecifier = herbEntity.cntntsSj.asc();
        }

        if("latest".equals(herbSort.getSort())){
            orderSpecifier = herbEntity.id.desc();
        }

        if("views".equals(herbSort.getSort()) && "desc".equals(herbSort.getOrderBy())){
            orderSpecifier = herbViewsEntity.viewCount.coalesce(0L).desc();
        }

        // 조건이 하나도 없다면 모든 목록 반환(있다면 builder 적용)
        return jpaQueryFactory
                .select(Projections.fields(
                        HerbDTO.class,
                        herbEntity.id,
                        herbEntity.bneNm,
                        herbEntity.cntntsSj,
                        herbEntity.hbdcNm,
                        herbEntity.imgUrl1,
                        herbViewsEntity.viewCount
                ))
                .from(herbEntity)
                .leftJoin(herbViewsEntity).on(herbViewsEntity.herb.eq(herbEntity))
                .where(builder)
                .offset(offset)
                .orderBy(orderSpecifier)
                .limit(pageInfoDTO.getSize())
                .fetch();
    }

    // 약초 개수 반환
    public Long countByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO){
        QHerbEntity herbEntity = QHerbEntity.herbEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리가 존재한다면,
        if(herbFilteringRequestDTO.getBneNm() != null && !herbFilteringRequestDTO.getBneNm().isEmpty()){
            builder.and(herbEntity.bneNm.contains(herbFilteringRequestDTO.getBneNm()));
        }

        // 개화기간이 존재 한다면
        if(herbFilteringRequestDTO.getMonth() !=null && !herbFilteringRequestDTO.getMonth().isEmpty()){
            builder.and(herbEntity.flowering.contains(herbFilteringRequestDTO.getMonth()));
        }

        return jpaQueryFactory.select(herbEntity.count())
                .from(herbEntity)
                .where(builder)
                .fetchOne();

    }

    // 조회수에 따른 약초 목록
    @Override
    public List<PopularityHerbsDTO> findAllByOrderByViewCountDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        return herbJpaRepository.findAllByOrderByViewCountDesc(pageable);
    }


}