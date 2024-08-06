package com.musinsa.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CacheCategoryMinMax {

    private String categoryId;
    private MinMaxPriceInfo minMaxPriceInfo;

    @Data
    @AllArgsConstructor
    public static class MinMaxPriceInfo {
        private PriceInfo min;
        private PriceInfo max;
    }

    @Data
    @AllArgsConstructor
    public static class PriceInfo {
        private Long brandId;
        private int price;
    }
}
