package com.mincho.herb.common.util;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class CommonUtils {

    // 에러 메시지 추출
    public String extractErrorMessage(BindingResult result){
        return Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
    }

    public Boolean emailValidation(String email){
        Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return pattern.matcher(email).matches();
    }

    // 유저 체크
    public String userCheck(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!this.emailValidation(email)){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "유효한 요청 권한이 없습니다.");
        }

        return email;

    }

    public String createAuthCode(int maxCodeLength){
        return UUID.randomUUID().toString().substring(0, maxCodeLength);
    }
}
