package com.musinsa.infra.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {
    CATEGORY_MIN_MAX("카테고리별 최소,최대 금액 상품 해쉬 키"),
    SINGLE_BRAND_MIN("브랜드별 최소 금액 상품 해쉬 키");

    private String desc;
}
