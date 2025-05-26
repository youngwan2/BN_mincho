package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.infra.auth.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;
    private final S3Service s3Service;

    /** 프로필 수정 */
    @PatchMapping("/me")
    public ResponseEntity<Map<String, String>> updateProfile(@Valid @RequestBody ProfileRequestDTO profileRequestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        profileService.updateProfile(profileRequestDTO, email);
        return new SuccessResponse<>().getResponse(200, "프로필이 수정되었습니다.", HttpSuccessType.OK);
    }

    /** 프로필 이미지 업로드 */
    @PatchMapping("/me/upload")
    public ResponseEntity<Void> uploadProfileImage(@RequestParam MultipartFile image) {
        log.info("프로필 이미지 업로드 요청: {}", image.getOriginalFilename());

        profileService.updateProfileImage(image);
        return ResponseEntity.noContent().build();
    }

    /** 프로필 조회 */
    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        ProfileResponseDTO profile = profileService.getUserProfile(email);
        log.info("조회된 프로필 정보: {}", profile);

        return new SuccessResponse<>().getResponse(200, "성공적으로 프로필 정보를 조회하였습니다.", HttpSuccessType.OK, profile);
    }
}
