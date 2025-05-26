package com.mincho.herb.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;


@Slf4j
@Component
public class AuthUtils {

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
    public boolean hasAdminRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ADMIN"));
    }

    public String createAuthCode(int maxCodeLength){
        return UUID.randomUUID().toString().substring(0, maxCodeLength);
    }
}
