package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import com.mincho.herb.global.config.error.ErrorResponse;
import com.mincho.herb.global.config.error.HttpErrorType;
import com.mincho.herb.global.config.success.HttpSuccessType;
import com.mincho.herb.global.config.success.SuccessResponse;
import com.mincho.herb.global.util.CommonUtils;
import com.mincho.herb.infra.auth.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;
    private final CommonUtils commonUtils;
    private final S3Service s3Service;

    // 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<Map<String, String>> updateProfile(@Valid @RequestBody ProfileRequestDTO profileRequestDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        profileService.updateProfile(profileRequestDTO, email);
        return new SuccessResponse<>().getResponse(200, "프로필이 수정 되었습니다.", HttpSuccessType.OK);
    }

    // 프로필 이미지 추가
    @PatchMapping("/me/upload")
    public ResponseEntity<Void> uploadProfileImage(
            @RequestParam MultipartFile image
            ){

        log.info("file{}", image.getOriginalFilename());
        String imageUrl = s3Service.upload(image);
        profileService.updateProfileImage(imageUrl);

        return ResponseEntity.noContent().build();
    }

    // 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        ProfileResponseDTO profile= profileService.getUserProfile(email);
        log.info("profile 정보: {}", profile);

        return new SuccessResponse<>().getResponse(200, "성공적으로 프로필 정보를 조회 하였습니다.", HttpSuccessType.OK, profile);
    }

}
