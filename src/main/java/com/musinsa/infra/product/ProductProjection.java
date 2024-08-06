package com.musinsa.infra.product;

public interface ProductProjection {
    Long getId();
    String getName();
    int getPrice();
    Long getCategoryId();
    Long getBrandId();
}
