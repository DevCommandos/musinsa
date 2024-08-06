package com.musinsa.domain.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CategoryMinPrice {
    private int totalPrice;
    private List<ProductInfo> productList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductInfo {
        private Long categoryId;
        private Long brandId;
        private String categoryName;
        private String brandName;
        private int price;

        public static ProductInfo of(Long categoryId, Long brandId, int price) {
            return ProductInfo.builder()
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .price(price)
                    .build();
        }
    }
}
