package com.mincho.herb.common.exception;

import com.mincho.herb.common.config.error.HttpErrorCode;

public class CustomHttpException  extends RuntimeException{

    private final HttpErrorCode httpErrorCode;

    public CustomHttpException(HttpErrorCode httpErrorCode, HttpErrorCode httpErrorCode1){
        super(httpErrorCode.getMessage());
        this.httpErrorCode = httpErrorCode1;
    }

    public HttpErrorCode getHttpErrorCode() {
        return httpErrorCode;
    }
}
