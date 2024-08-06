package com.musinsa.domain.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    NO_PRODUCTS_IN_CATEGORY("해당 카테고리에 대한 상품이 없습니다.", "1001", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다.", "1002", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다.", "1003", HttpStatus.NOT_FOUND);

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

}
