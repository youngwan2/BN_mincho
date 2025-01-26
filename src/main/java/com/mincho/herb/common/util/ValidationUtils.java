package com.mincho.herb.common.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Objects;
import java.util.UUID;

@Component
public class ValidationUtils {

    public String extractErrorMessage(BindingResult result){
        return Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
    }

    public String createAuthCode(int maxCodeLength){
        return UUID.randomUUID().toString().substring(0, maxCodeLength);
    }
}
