package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbAdminRepository;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import com.mincho.herb.global.util.MapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HerbAdminServiceImpl implements HerbAdminService {
    private final HerbAdminRepository herbAdminRepository;
    private final HerbRepository herbRepository;
    private final UserService userService;
    private final MapperUtils mapperUtils;
    private final HerbImageService herbImageService;
    private final AuthUtils authUtils;


    // TODO: 이미지의 경우 HerbImageEntity 를 따로 만들어서 관리하는게 좋지만 빠른 구현을 위해 유지
    // 약초 등록
    @Override
    @Transactional
    public void createHerb(HerbCreateRequestDTO herbCreateRequestDTO, List<MultipartFile> imageFiles) {
       String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if(roles.equalsIgnoreCase("ROLE_ADMIN")){
           throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
       }
       String email = authUtils.userCheck();
       if(email == null){
           throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "로그인 후 이용 가능합니다.");
       }
       Long adminId = userService.getUserByEmail(email).getId();
        HerbEntity unsavedHerbEntity = HerbEntity.builder()
                .cntntsSj(herbCreateRequestDTO.getCntntsSj()) // 약초명
                .hbdcNm(herbCreateRequestDTO.getHbdcNm()) // 한방명
                .useeRegn(herbCreateRequestDTO.getUseeRegn()) // 이용부위
                .prvateTherpy(herbCreateRequestDTO.getPrvateTherpy()) // 민간요법
                .bneNm(herbCreateRequestDTO.getBneNm()) // 약초학명
                .growthForm(herbCreateRequestDTO.getGrowthForm()) // 생장 형태
                .flowering(herbCreateRequestDTO.getFlowering()) // 개화기
                .habitat(herbCreateRequestDTO.getHabitat()) // 분포 및 환경
                .harvest(herbCreateRequestDTO.getHarvest()) // 수확 및 건조
                .adminId(adminId)
                .build();

        HerbEntity herbEntity= herbAdminRepository.save(unsavedHerbEntity);

        // 업로드할 이미지가 있으면 동적 업로드 처리
        if(!imageFiles.isEmpty()){
            List<String> imgUrls = herbImageService.uploadHerbImages(imageFiles, herbEntity.getId());

            for (int i = 0; i < imgUrls.size(); i++) {
                String url = imgUrls.get(i);
                switch (i) {
                    case 0 -> herbEntity.setImgUrl1(url);
                    case 1 -> herbEntity.setImgUrl2(url);
                    case 2 -> herbEntity.setImgUrl3(url);
                    case 3 -> herbEntity.setImgUrl4(url);
                    case 4 -> herbEntity.setImgUrl5(url);
                    case 5 -> herbEntity.setImgUrl6(url);
                }
            }
        }
    }


    // 약초 제거
    @Override
    @Transactional
    public void removeHerb(Long id) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        HerbEntity herbEntity = herbRepository.findById(id);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");

        }
        herbImageService.deleteHerbImages(herbImageService.findHerbImageUrlsByHerbId(id)); // 모든 이미지를 S3에서 제거

        herbAdminRepository.deleteById(id);
    }

    // 약초 수정
    @Override
    @Transactional
    public void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO,List<MultipartFile> imageFiles, Long herbId) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        String email = authUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "로그인 후 이용 가능합니다.");
        }

        HerbEntity oldHerbEntity = herbAdminRepository.removeHerbImagesByHerbId(herbId); // 전체 이미지 null 초기화 및 herbEntity 조회

        Long adminId = userService.getUserByEmail(email).getId();
        oldHerbEntity.setCntntsSj(herbUpdateRequestDTO.getCntntsSj());       // 약초명
        oldHerbEntity.setHbdcNm(herbUpdateRequestDTO.getHbdcNm());           // 한방명
        oldHerbEntity.setUseeRegn(herbUpdateRequestDTO.getUseeRegn());       // 이용부위
        oldHerbEntity.setPrvateTherpy(herbUpdateRequestDTO.getPrvateTherpy()); // 민간요법
        oldHerbEntity.setBneNm(herbUpdateRequestDTO.getBneNm());             // 약초학명
        oldHerbEntity.setGrowthForm(herbUpdateRequestDTO.getGrowthForm());   // 생장 형태
        oldHerbEntity.setFlowering(herbUpdateRequestDTO.getFlowering());     // 개화기
        oldHerbEntity.setHabitat(herbUpdateRequestDTO.getHabitat());         // 분포 및 환경
        oldHerbEntity.setHarvest(herbUpdateRequestDTO.getHarvest());         // 수확 및 건조
        oldHerbEntity.setAdminId(adminId);                                   // 관리자 ID


        // 새로 업로드할 이미지가 있으면 동적 업로드 처리
        if(!imageFiles.isEmpty()){
            List<String> imgUrls = herbImageService.uploadHerbImages(imageFiles, oldHerbEntity.getId());

            for (int i = 0; i < imgUrls.size(); i++) {
                String url = imgUrls.get(i);
                switch (i) {
                    case 0 -> oldHerbEntity.setImgUrl1(url);
                    case 1 -> oldHerbEntity.setImgUrl2(url);
                    case 2 -> oldHerbEntity.setImgUrl3(url);
                    case 3 -> oldHerbEntity.setImgUrl4(url);
                    case 4 -> oldHerbEntity.setImgUrl5(url);
                    case 5 -> oldHerbEntity.setImgUrl6(url);
                }
            }
        }

        herbAdminRepository.save(oldHerbEntity);
    }

    // 값 초기화
    @Override
    public void insertMany() throws IOException {
        List<Herb> herbs = mapperUtils.jsonMapper("herb.json", Herb.class);
        List<HerbEntity> herbsEntity = herbs.stream().map(herb -> {
            herb.setAdminId(-999L);
            return HerbEntity.toEntity(herb);
        }).toList();
        herbAdminRepository.saveAll(herbsEntity);

    }
}
