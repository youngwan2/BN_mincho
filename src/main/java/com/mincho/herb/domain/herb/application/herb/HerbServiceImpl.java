package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
    public List<Herb> getHerbSummary(int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<HerbEntity> herbEntities = herbRepository.findAllPaging(pageable);

        if(herbEntities.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }

        return herbEntities.stream().map(HerbEntity::toModel).toList();
    }
    
    // 상세 페이지
    @Override
    public Herb getHerbDetails(Long id) {
        return herbRepository.findById(id).toModel();
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
        log.info("로그인된 유저의 이메일:{}", email);

        Long adminId = userRepository.findByEmail(email).getId();

        HerbEntity herbEntity = herbRepository.findById(herbId);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");
        }

        HerbEntity unsavedHerbEntity =  HerbEntity.builder()
                .id(herbId)
                .cntntsNo(herbUpdateRequestDTO.getCntntsNo())
                .cntntsSj(herbUpdateRequestDTO.getCntntsSj())
                .imgUrl1(herbUpdateRequestDTO.getImgUrl1())
                .imgUrl2(herbUpdateRequestDTO.getImgUrl2())
                .imgUrl3(herbUpdateRequestDTO.getImgUrl3())
                .imgUrl4(herbUpdateRequestDTO.getImgUrl4())
                .imgUrl5(herbUpdateRequestDTO.getImgUrl5())
                .imgUrl6(herbUpdateRequestDTO.getImgUrl6())
                .stle(herbUpdateRequestDTO.getStle())
                .hbdcNm(herbUpdateRequestDTO.getHbdcNm())
                .useeRegn(herbUpdateRequestDTO.getUseeRegn())
                .prvateTherpy(herbUpdateRequestDTO.getPrvateTherpy())
                .adminId(adminId)
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
}
