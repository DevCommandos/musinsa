package com.musinsa.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CacheBrandCategoryMin {
    private String brand;
    private List<CategoryPriceInfo> categoryList;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryPriceInfo {
        private String categoryId;
        private int price;
    }
}
