package com.musinsa.present;

import com.musinsa.domain.Exception.CustomException;
import com.musinsa.present.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<?>> handleCustomException(CustomException ex) {
        ResponseDto<?> error = ResponseDto.error(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto<?>> handleRuntimeException(Exception e) {
        log.error("error occurred : ", e);
        String errMessage = e.getMessage() == null ? "An unexpected error occurred" : e.getMessage();
        ResponseDto<?> error = ResponseDto.error(errMessage);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleGeneralException(Exception e) {
        log.error("error occurred : ", e);
        String errMessage = e.getMessage() == null ? "An unexpected error occurred" : e.getMessage();
        ResponseDto<?> error = ResponseDto.error(errMessage);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
