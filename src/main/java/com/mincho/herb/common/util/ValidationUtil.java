package com.mincho.herb.common.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Component
public class ValidationUtil {

    public String extractErrorMessage(BindingResult result){
        return Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
    }
}
