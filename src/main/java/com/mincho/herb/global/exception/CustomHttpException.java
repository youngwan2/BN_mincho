package com.mincho.herb.global.exception;

import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.Getter;

@Getter
public class CustomHttpException  extends RuntimeException{

    private final HttpErrorCode httpErrorCode;

    public CustomHttpException(HttpErrorCode httpErrorCode, String message){
        super(message ==null ? httpErrorCode.getMessage(): message);
        this.httpErrorCode = httpErrorCode;
    }

}
