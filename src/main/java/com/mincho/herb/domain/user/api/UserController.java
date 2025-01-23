package com.mincho.herb.domain.user.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.ValidationUtil;
import com.mincho.herb.domain.user.application.UserServiceImpl;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestLoginDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;
import com.mincho.herb.infra.auth.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final ValidationUtil validationUtil;
    private final CookieUtil cookieUtil;
    private final UserServiceImpl userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RequestRegisterDTO registerDTO, BindingResult result){

        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }
        log.info("userinfo {}", registerDTO);
        userService.register(registerDTO);

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
}
