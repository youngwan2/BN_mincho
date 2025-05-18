package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.domain.HerbViews;
import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbViewsEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.herb.repository.herbViews.HerbViewsRepository;
import com.mincho.herb.global.aop.UserActivityAction;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.dto.PageInfoDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HerbQueryServiceImpl implements HerbQueryService{

    private final HerbRepository herbRepository;
    private final HerbViewsRepository herbViewsRepository;

    // 약초명으로 약초 찾기
    @Override
    public HerbEntity getHerbByHerbName(String herbName) {
        HerbEntity herbEntity =  herbRepository.findByCntntsSj(herbName);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회 데이터가 없습니다.");
        }
        return herbEntity;
    }

    // ID 로 약초 찾기
    @Override
    public HerbEntity getHerbById(Long id) {
        HerbEntity herbEntity =  herbRepository.findById(id);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회 데이터가 없습니다.");
        }
        return herbEntity;
    }


    // 약초 목록 조회(페이징)
    @Override
    public List<HerbDTO> getHerbs(PageInfoDTO pageInfoDTO, HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort) {

        List<HerbDTO> herbs = herbRepository.findByFiltering(herbFilteringRequestDTO, herbSort, pageInfoDTO);
        if(herbs.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }

        return herbs;

    }


    // 상세 페이지
    @Override
    @UserActivityAction(action = "herb_detail")
    public HerbDetailResponseDTO getHerbDetails(Long id) {

        HerbEntity herbEntity = herbRepository.findById(id); // 조회 약초 엔티티
        HerbViewsEntity herbViewsEntity = herbViewsRepository.findByHerb(herbEntity); // 이전 조회수 엔티티

        // 허브 조회수가 null 이면 새로 만들어서 초기화 해줌
        if(herbViewsEntity ==null){
            HerbViewsEntity newHerbViews = new HerbViewsEntity();
            newHerbViews.setViewCount(0L);
            newHerbViews.setHerb(herbEntity);
            herbViewsEntity = herbViewsRepository.save(newHerbViews);
        }

        // 이미지
        List<String> images = List.of(
                herbEntity.getImgUrl1(),
                herbEntity.getImgUrl2(),
                herbEntity.getImgUrl3(),
                herbEntity.getImgUrl4(),
                herbEntity.getImgUrl5(),
                herbEntity.getImgUrl6()
        );

        HerbViews herbViews = herbViewsEntity.toModel();
        Long updatedViewCount =herbViews.increase(herbViewsEntity.getViewCount()); // 조회수 증가

        herbViewsRepository.save(HerbViewsEntity.toEntity(herbViews, herbEntity));


        return HerbDetailResponseDTO.builder()
                .id(herbEntity.getId())
                .bneNm(herbEntity.getBneNm())
                .cntntsNo(herbEntity.getCntntsNo())
                .cntntsSj(herbEntity.getCntntsSj())
                .prvateTherpy(herbEntity.getPrvateTherpy())
                .hbdcNm(herbEntity.getHbdcNm())
                .imgUrls(images)
                .viewCount(updatedViewCount)
                .useeRegn(herbEntity.getUseeRegn())
                .growthForm(herbEntity.getGrowthForm())
                .flowering(herbEntity.getFlowering())
                .habitat(herbEntity.getHabitat())
                .harvest(herbEntity.getHarvest())
                .createdAt(herbEntity.getCreatedAt())
                .updatedAt(herbEntity.getUpdatedAt())
                .build();

    }

    // 이달의 개화 약초
    @Override
    public List<HerbDTO> getHerbsBloomingThisMonth(String month) {

        List<HerbEntity> herbEntities  = herbRepository.findByMonth(month);
        return herbEntities.stream().map((herbEntity)->{
            return HerbDTO.builder()
                    .id(herbEntity.getId())
                    .bneNm(herbEntity.getBneNm())
                    .imgUrl1(herbEntity.getImgUrl1())
                    .cntntsSj(herbEntity.getCntntsSj())
                    .hbdcNm(herbEntity.getHbdcNm())
                    .build();

        }).toList();

    }

    // 실시간 인기 순위 약초
    @Override
    public List<PopularityHerbsDTO> getHerbsMostview() {
        return herbRepository.findAllByOrderByViewCountDesc();
    }
    // 랜덤 약초
    @Override
    public List<HerbDTO> getRandomHerbs(Long herbId) {

        // 현재 조회중인 허브를 제외한 모든 허브 id 목록 조회
        List<Long> herbIds = herbRepository.findHerbIds()
                .stream()
                .filter(id -> !Objects.equals(id, herbId))
                .toList();

        // 랜덤하게 3개 뽑기
        List<Integer> randomIds = new ArrayList<>();

        // 뽑았는데 중복이라면? 중복된 값이 안 나올 때 까지 반복 돌려야 하는거 아님?
        while(randomIds.size() <3){
            int random0to3 = (int) (Math.random() * herbIds.size()); // 랜덤하게 한 개 뽑고.
            randomIds.add(random0to3);

            // 뽑은 값이 기존 리스트에 있는감? 중복이 아니면 추가
            if(!randomIds.contains(random0to3)){
                int random = (int) (Math.random() * herbIds.size()); // 다시 뽑아서
                randomIds.add(random);
            }
        }


        int index1 = randomIds.get(0);
        int index2 = randomIds.get(1);
        int index3 = randomIds.get(2);

        List<HerbEntity> herbEntities = herbRepository.findRandom(herbIds.get(index1), herbIds.get(index2), herbIds.get(index3));

        return herbEntities.stream().map((herbEntity)->{
            return HerbDTO.builder()
                    .id(herbEntity.getId())
                    .bneNm(herbEntity.getBneNm())
                    .imgUrl1(herbEntity.getImgUrl1())
                    .cntntsSj(herbEntity.getCntntsSj())
                    .hbdcNm(herbEntity.getHbdcNm())
                    .build();

        }).toList();
    }

    // 약초 전체 개수
    @Override
    public Long getHerbCount(HerbFilteringRequestDTO herbFilteringRequestDTO) {
        return herbRepository.countByFiltering(herbFilteringRequestDTO);
    }

}
