package com.mincho.herb.common.exception;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgNotValidException(MethodArgumentNotValidException ex){

        return new ErrorResponse().getResponse(ex.getStatusCode().value(), ex.getMessage(), ex.getBody().getTitle());
    }

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<Map<String, String>> handleCustomHttpException(CustomHttpException ex){

        int httpStatus =  ex.getHttpErrorCode().getHttpStatus();
        HttpErrorType httpErrorType =  ex.getHttpErrorCode().getErrorType();

        return new ErrorResponse().getResponse(httpStatus, ex.getMessage(), httpErrorType);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex){

        return new ErrorResponse().getResponse(500, "서버에서 문제가 발생하였습니다. 나중에 다시시도해 주세요.", HttpErrorType.INTERNAL_SERVER_ERROR);
    }
}
