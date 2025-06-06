package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.LoginRequestDTO;
import com.mincho.herb.domain.user.dto.RegisterRequestDTO;
import com.mincho.herb.domain.user.dto.UpdatePasswordRequestDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.global.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final CookieUtils cookieUtils;
    private final UserService userService;
    private final ProfileService profileService;

    /** 회원가입 */
    @Operation(summary = "회원가입", description = "신규 유저 회원가입 API")
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Parameter(description = "회원가입 정보") @Valid @RequestBody RegisterRequestDTO registerDTO) {
        log.info("회원가입 요청: {}", registerDTO);

        User savedUser = userService.register(registerDTO);
        profileService.insertProfile(savedUser);

        return new SuccessResponse<>().getResponse(201, "등록되었습니다.", HttpSuccessType.CREATED);
    }

    /** 중복 확인 */
    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일 중복 체크 API")
    @PostMapping("/register/duplicate-check")
    public ResponseEntity<Map<String, String>> checkDuplicate(
            @Parameter(description = "이메일 중복 확인 정보") @Valid @RequestBody DuplicateCheckDTO dto) {
        boolean isDuplicate = userService.dueCheck(dto);
        if (isDuplicate) {
            return new ErrorResponse().getResponse(409, "존재하는 이메일입니다.", HttpErrorType.CONFLICT);
        }
        return new SuccessResponse<>().getResponse(200, "회원가입을 진행해주세요.", HttpSuccessType.OK);
    }

    /** 로그인 */
    @Operation(summary = "로그인", description = "유저 로그인 API")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보(이메일, 비밀번호)") @Valid @RequestBody LoginRequestDTO loginDTO,
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response) {
        Map<String, String> tokenMap = userService.login(loginDTO);

        response.addCookie(cookieUtils.createCookie("refresh", tokenMap.get("refresh"), 60 * 60 * 24));

        Map<String, String> body = Map.of("message", "로그인 되었습니다.");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenMap.get("access"))
                .body(body);
    }

    /** 회원 탈퇴 */
    @Operation(summary = "회원 탈퇴", description = "유저 회원 탈퇴 API")
    @DeleteMapping("/me/entire")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response) {
        String email = getCurrentEmail();

        userService.deleteUser(email);
        response.addCookie(cookieUtils.createCookie("refresh", "", 0));

        return new SuccessResponse<>().getResponse(200, "정상 탈퇴되었습니다.", HttpSuccessType.OK);
    }

    /** 비밀번호 수정 */
    @Operation(summary = "비밀번호 수정", description = "유저 비밀번호 수정 API")
    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @Parameter(description = "비밀번호 수정 정보(현재 비밀번호, 새 비밀번호)") @Valid @RequestBody UpdatePasswordRequestDTO dto) {
        String email = getCurrentEmail();

        boolean validCurrentPassword = userService.checkPassword(email, dto.getCurrentPassword());
        if (!validCurrentPassword) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 현재 비밀번호입니다.");
        }

        userService.updatePassword(email, dto.getNewPassword());
        return new SuccessResponse<>().getResponse(200, "비밀번호가 수정되었습니다.", HttpSuccessType.OK);
    }

    /** 로그아웃 */
    @Operation(summary = "로그아웃", description = "유저 로그아웃 API")
    @DeleteMapping("/me/logout")
    public ResponseEntity<Map<String, String>> logout(
            @Parameter(description = "HTTP 응답 객체") HttpServletResponse response) {
        response.addCookie(cookieUtils.createCookie("refresh", null, 0));
        return new SuccessResponse<>().getResponse(200, "로그아웃 되었습니다.", HttpSuccessType.OK);
    }

    /** 로그인 상태 확인 */
    @Operation(summary = "로그인 상태 확인", description = "유저 로그인 상태 확인 API")
    @GetMapping("/login-status")
    public ResponseEntity<Boolean> isLogin() {
        return ResponseEntity.ok(userService.isLogin());
    }

    /** 현재 인증된 유저 이메일 반환 */
    private String getCurrentEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "인증된 유저가 아닙니다.");
        }
        return email;
    }
}
