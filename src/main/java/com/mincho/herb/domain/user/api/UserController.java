package com.mincho.herb.domain.user.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.ValidationUtil;
import com.mincho.herb.domain.user.application.profile.ProfileService;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.application.user.UserServiceImpl;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.*;
import com.mincho.herb.infra.auth.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final ValidationUtil validationUtil;
    private final CookieUtil cookieUtil;
    private final UserService userService;
    private final ProfileService profileService;

    // 회원가입
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RequestRegisterDTO registerDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        log.info("userinfo {}", registerDTO);
        User savedUser = userService.register(registerDTO);
        profileService.insertProfile(savedUser);


        log.info("state:{}","회원가입 성공!");
        return new SuccessResponse<>().getResponse(201,"등록 되었습니다.", HttpSuccessType.OK);
    }

    // 회원중복 확인
    @PostMapping("/register/duplicate-check")
    public ResponseEntity<Map<String, String>> dueCheck(@Valid @RequestBody DuplicateCheckDTO duplicateCheckDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        boolean isDue = userService.dueCheck(duplicateCheckDTO);

        if(isDue){
            return new ErrorResponse().getResponse(409, "존재하는 이메일 입니다.", HttpErrorType.CONFLICT);
        }

        return new SuccessResponse<>().getResponse(200, "회원가입을 진행해주세요", HttpSuccessType.OK);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody RequestLoginDTO requestLoginDTO, BindingResult result, HttpServletResponse response){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        Map<String, String> tokenMap= userService.login(requestLoginDTO);
        response.addCookie(cookieUtil.createCookie("refresh", tokenMap.get("refresh"), 60*60*24));

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+tokenMap.get("access"))
                .body("{\"message\":\"로그인 되었습니다.\"}");
    }

    // 회원탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteUser(HttpServletResponse response){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(email);

        response.addCookie(cookieUtil.createCookie("refresh", "", 0));
        return new SuccessResponse<>().getResponse(200, "정상 탈퇴되었습니다.", HttpSuccessType.OK);
    }


    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(@Valid @RequestBody RequestUpdatePassword requestUpdatePassword, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updatePassword(requestUpdatePassword.getPassword(), email);
        return new SuccessResponse<>().getResponse(200, "비밀번호가 수정 되었습니다.", HttpSuccessType.OK);
    }
}
