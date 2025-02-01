package com.mincho.herb.common.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class CommonUtils {

    public String extractErrorMessage(BindingResult result){
        return Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
    }

    public boolean emailValidation(String email){
        Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return pattern.matcher(email).matches();
    }

    public String createAuthCode(int maxCodeLength){
        return UUID.randomUUID().toString().substring(0, maxCodeLength);
    }
}
