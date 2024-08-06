package com.musinsa.domain.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CategoryMinMaxPrice {
    private String categoryName;
    private Long categoryId;
    private List<PriceInfo> minPrice;
    private List<PriceInfo> maxPrice;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PriceInfo {
        private String brandName;
        private Long brandId;
        private int price;
    }
}
