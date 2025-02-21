package com.mincho.herb.domain.user.api;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<Map<String, String>> updateProfile(@Valid @RequestBody ProfileRequestDTO profileRequestDTO){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }
        profileService.updateProfile(profileRequestDTO, email);
        return new SuccessResponse<>().getResponse(200, "프로필이 수정 되었습니다.", HttpSuccessType.OK);
    }

    // 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        Profile profile= profileService.getUserProfile(email);

        return new SuccessResponse<>().getResponse(200, "성공적으로 프로필 정보를 조회 하였습니다.", HttpSuccessType.OK, profile);

    }

}
