package com.musinsa.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductModel {

    private Long productId;

    private String name;

    private int price;

    private Long categoryId;

    private Long brandId;

    public static ProductModel of(Long productId, String name, int price, Long categoryId, Long brandId) {
        return ProductModel.builder()
                .productId(productId)
                .name(name)
                .price(price)
                .categoryId(categoryId)
                .brandId(brandId)
                .build();
    }
}