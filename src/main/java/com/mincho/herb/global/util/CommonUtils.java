package com.mincho.herb.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;


// TODO: 관심사가 불분명한 클래스. 향후 정리 필요
@Slf4j
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
        log.info("usercheck:{}", email);
        if(!email.contains("@")){
            return null;
        }

        return email;

    }


    public String createAuthCode(int maxCodeLength){
        return UUID.randomUUID().toString().substring(0, maxCodeLength);
    }
}
