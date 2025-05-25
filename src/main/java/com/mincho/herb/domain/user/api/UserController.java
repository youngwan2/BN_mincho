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
import com.mincho.herb.global.util.CommonUtils;
import com.mincho.herb.global.util.CookieUtils;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final CommonUtils commonUtils;
    private final CookieUtils cookieUtils;
    private final UserService userService;
    private final ProfileService profileService;

    // 회원가입
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RegisterRequestDTO registerDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        log.info("userinfo {}", registerDTO);
        User savedUser = userService.register(registerDTO);
        profileService.insertProfile(savedUser);


        log.info("state:{}","회원가입 성공!");
        return new SuccessResponse<>().getResponse(201,"등록 되었습니다.", HttpSuccessType.CREATED);
    }

    // 회원중복 확인
    @PostMapping("/register/duplicate-check")
    public ResponseEntity<Map<String, String>> dueCheck(@Valid @RequestBody DuplicateCheckDTO duplicateCheckDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        boolean isDue = userService.dueCheck(duplicateCheckDTO);

        if(isDue){
            return new ErrorResponse().getResponse(409, "존재하는 이메일 입니다.", HttpErrorType.CONFLICT);
        }

        return new SuccessResponse<>().getResponse(200, "회원가입을 진행해주세요", HttpSuccessType.OK);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult result, HttpServletResponse response){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        Map<String, String> tokenMap= userService.login(loginRequestDTO);
        response.addCookie(cookieUtils.createCookie("refresh", tokenMap.get("refresh"), 60*60*24));

        Map<String, String> body = new HashMap<>();
        body.put("message", "로그인 되었습니다.");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+tokenMap.get("access"))
                .body(body);
    }

    // 회원탈퇴
    @DeleteMapping("/me/entire")
    public ResponseEntity<Map<String, String>> deleteUser(HttpServletResponse response){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }


        userService.deleteUser(email);

        response.addCookie(cookieUtils.createCookie("refresh", "", 0));
        return new SuccessResponse<>().getResponse(200, "정상 탈퇴되었습니다.", HttpSuccessType.OK);
    }


    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO){

        log.info("요청 비밀번호:{}", updatePasswordRequestDTO);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        // 현재 비밀번호가 유효한 비밀번호인가?
        boolean pass = userService.checkPassword(email, updatePasswordRequestDTO.getCurrentPassword());

        // Yes! 유효함
        if(pass){
            userService.updatePassword(email, updatePasswordRequestDTO.getNewPassword());
            return new SuccessResponse<>().getResponse(200, "비밀번호가 수정 되었습니다.", HttpSuccessType.OK);

            // No! 유효하지 않음
        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 현재 비밀번호 입니다.");
        }
    }

    // 로그아웃
    @DeleteMapping("/me/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletResponse response
             ){

        response.addCookie(cookieUtils.createCookie("refresh",null, 0));
        return new SuccessResponse<>().getResponse(200, "로그아웃 되었습니다.", HttpSuccessType.OK);
    }


    // 로그인 상태 확인
    @GetMapping("/login-status")
    public ResponseEntity<Boolean> isLogin(){
        boolean isLogin = userService.isLogin();
        return ResponseEntity.ok(isLogin);

    }
}
