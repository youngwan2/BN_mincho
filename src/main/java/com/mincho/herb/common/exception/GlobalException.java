package com.mincho.herb.common.exception;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<Map<String, String>> handleCustomHttpException(CustomHttpException ex){

        int httpStatus =  ex.getHttpErrorCode().getHttpStatus();
        HttpErrorType httpErrorType =  ex.getHttpErrorCode().getErrorType();

        return new ErrorResponse().getResponse(httpStatus, ex.getMessage(), httpErrorType);
    }
}
