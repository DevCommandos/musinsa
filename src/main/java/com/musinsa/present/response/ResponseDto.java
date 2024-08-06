package com.musinsa.present.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseDto<T> {

    private static final String SUCCESS_CODE = "0000";
    private static final String SUCCESS_MESSAGE = "요청 성공";

    private static final String FAIL_CODE = "9999";

    private String code;
    private String message;
    private T result;

    public static <T> ResponseDto success(T body) {
        return ResponseDto.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(body)
                .build();
    }

    public static <T> ResponseDto success() {
        return ResponseDto.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(null)
                .build();
    }

    public static ResponseDto error(String code, String message) {
        return ResponseDto.builder()
                .code(code)
                .message(message)
                .result(null)
                .build();
    }

    public static ResponseDto error(String message) {
        return ResponseDto.builder()
                .code(FAIL_CODE)
                .message(message)
                .result(null)
                .build();
    }
}
