package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import com.mincho.herb.domain.user.dto.ProfileSummaryDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.infra.auth.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "프로필", description = "프로필 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;
    private final S3Service s3Service;



    /** 프로필 수정 */
    @Operation(summary = "프로필 수정", description = "유저 프로필 정보 수정 API")
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
    @Operation(summary = "프로필 이미지 업로드", description = "유저 프로필 이미지 업로드 API")
    @PatchMapping("/me/upload")
    public ResponseEntity<Void> uploadProfileImage(@RequestParam MultipartFile image) {

        profileService.updateProfileImage(image);
        return ResponseEntity.noContent().build();
    }

    /** 프로필 조회 */
    @Operation(summary = "프로필 조회", description = "유저 프로필 정보 조회 API")
    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        ProfileResponseDTO profile = profileService.getUserProfile(email);

        return new SuccessResponse<>().getResponse(200, "성공적으로 프로필 정보를 조회하였습니다.", HttpSuccessType.OK, profile);
    }


    /** 특정 유저 프로필 조회 */
    @Operation(summary = "특정 유저 프로필 조회", description = "유저 ID로 프로필 정보 조회 API")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileSummaryDTO> getUserProfile(
            @Parameter(required = true, description = "유저 식별 ID") @PathVariable Long userId) {
        ProfileSummaryDTO profile = profileService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }
}
