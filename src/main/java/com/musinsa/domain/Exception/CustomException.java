package com.musinsa.domain.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{

    private final String code;
    private final HttpStatus httpStatus;

    public CustomException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
        this.httpStatus = exceptionEnum.getHttpStatus();
    }

    public CustomException(String message) {
        super(message);
        this.code = null;
        this.httpStatus = null;
    }
}
