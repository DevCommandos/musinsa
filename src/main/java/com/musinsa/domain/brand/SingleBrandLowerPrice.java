package com.musinsa.domain.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SingleBrandLowerPrice {

    private LowestPrice lowestPrice;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LowestPrice {
        private String brandName;
        private Long brandId;
        private List<CategoryPrice> categoryList;
        private int totalAmount;

        public static LowestPrice of(String brandId, List<CategoryPrice> categoryListList, int totalAmount) {
            return LowestPrice.builder()
                    .brandId(Long.valueOf(brandId))
                    .categoryList(categoryListList)
                    .totalAmount(totalAmount)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CategoryPrice {
        private String categoryName;
        private Long categoryId;
        private int price;

        public static CategoryPrice of(String categoryId, int price) {
            return CategoryPrice.builder()
                    .categoryId(Long.valueOf(categoryId))
                    .price(price)
                    .build();
        }
    }
}
