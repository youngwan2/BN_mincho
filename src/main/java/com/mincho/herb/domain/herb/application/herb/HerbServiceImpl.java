package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class HerbServiceImpl implements HerbService{
    private final HerbRepository herbRepository;
    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;


    // 약초 등록
    @Override
    public void createHerb(HerbCreateRequestDTO herbCreateRequestDTO) {
       String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if(roles.equalsIgnoreCase("ROLE_ADMIN")){
           throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
       }

       String email = (String) SecurityContextHolder.getContext().getAuthentication().getName();
       log.info("로그인된 유저의 이메일:{}", email);
       Long adminId = userRepository.findByEmail(email).getId();

       HerbEntity unsavedHerbEntity =  HerbEntity.builder()
                .cntntsNo(herbCreateRequestDTO.getCntntsNo())
                .cntntsSj(herbCreateRequestDTO.getCntntsSj())
                .imgUrl1(herbCreateRequestDTO.getImgUrl1())
                .imgUrl2(herbCreateRequestDTO.getImgUrl2())
                .imgUrl3(herbCreateRequestDTO.getImgUrl3())
                .imgUrl4(herbCreateRequestDTO.getImgUrl4())
                .imgUrl5(herbCreateRequestDTO.getImgUrl5())
                .imgUrl6(herbCreateRequestDTO.getImgUrl6())
                .stle(herbCreateRequestDTO.getStle())
                .hbdcNm(herbCreateRequestDTO.getHbdcNm())
                .useeRegn(herbCreateRequestDTO.getUseeRegn())
                .prvateTherpy(herbCreateRequestDTO.getPrvateTherpy())
                .adminId(adminId)
                .bneNm(herbCreateRequestDTO.getBneNm())
                .build();
        herbRepository.save(unsavedHerbEntity);
    }

    // 약초명으로 약초 찾기
    @Override
    public Herb getHerbByHerbName(String herbName) {
        HerbEntity herbEntity =  herbRepository.findByCntntsSj(herbName);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회 데이터가 없습니다.");
        }
        return herbEntity.toModel();
    }


    // 약초 목록 조회(페이징)
    @Override
    public List<HerbDTO> getHerbs(PageInfoDTO pageInfoDTO, HerbFilteringRequestDTO herbFilteringRequestDTO) {

        List<HerbDTO> herbs = herbRepository.findByFiltering(herbFilteringRequestDTO, pageInfoDTO);
        if(herbs.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }

        return herbs;

    }


    // 상세 페이지
    @Override
    public HerbDetailResponseDTO getHerbDetails(Long id) {

         HerbEntity herbEntity = herbRepository.findById(id);

         List<String> images = List.of(
                 herbEntity.getImgUrl1(),
                 herbEntity.getImgUrl2(),
                 herbEntity.getImgUrl3(),
                 herbEntity.getImgUrl4(),
                 herbEntity.getImgUrl5(),
                 herbEntity.getImgUrl6()
         );

        return HerbDetailResponseDTO.builder()
                .id(herbEntity.getId())
                .bneNm(herbEntity.getBneNm())
                .cntntsNo(herbEntity.getCntntsNo())
                .cntntsSj(herbEntity.getCntntsSj())
                .prvateTherpy(herbEntity.getPrvateTherpy())
                .hbdcNm(herbEntity.getHbdcNm())
                .stle(herbEntity.getStle())
                .imgUrls(images)
                .useeRegn(herbEntity.getUseeRegn())
                .build();

    }

    // 약초 제거
    @Override
    public void removeHerb(Long id) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        HerbEntity herbEntity = herbRepository.findById(id);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");

        }

        log.info("roles: {}", roles);
        herbRepository.deleteById(id);
    }

    // 허브 수정
    @Override
    public void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO, Long herbId) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getName();

        Long adminId = userRepository.findByEmail(email).getId();

        HerbEntity herbEntity = herbRepository.findById(herbId);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");
        }

        HerbEntity unsavedHerbEntity =  HerbEntity.builder()
                .id(herbId)
                .cntntsSj(herbUpdateRequestDTO.getCntntsSj())
                .imgUrl1(herbUpdateRequestDTO.getImgUrl1())
                .hbdcNm(herbUpdateRequestDTO.getHbdcNm())
                .bneNm(herbUpdateRequestDTO.getBneNm())
                .build();

        herbRepository.save(unsavedHerbEntity);
    }

    // 값 초기화
    @Override
    public void insertMany() throws IOException {
        List<Herb> herbs = mapperUtils.jsonMapper("herb.json", Herb.class);
        List<HerbEntity> herbsEntity = herbs.stream().map(herb -> {
            herb.setAdminId(-999L);
            return HerbEntity.toEntity(herb);
        }).toList();
        herbRepository.saveAll(herbsEntity);

        log.info("mapping herb: {}",herbs.get(0));
    }

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

    // 약초 전체 개수
    @Override
    public Long getHerbCount(HerbFilteringRequestDTO herbFilteringRequestDTO) {
        return herbRepository.countByFiltering(herbFilteringRequestDTO);
    }
}
