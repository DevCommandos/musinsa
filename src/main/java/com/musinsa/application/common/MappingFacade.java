package com.musinsa.application.common;

import com.musinsa.domain.brand.BrandModel;
import com.musinsa.domain.brand.BrandService;
import com.musinsa.domain.category.CategoryModel;
import com.musinsa.domain.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MappingFacade {

    private final CategoryService categoryService;
    private final BrandService brandService;

    public Map<Long, String> getCategoryMap() {
        List<CategoryModel> findCategoryList = categoryService.findByAll();
        return findCategoryList.stream()
                .collect(Collectors.toMap(CategoryModel::getCategoryId, CategoryModel::getName));
    }

    public Map<Long, String> getBrandMap() {
        List<BrandModel> findBrandList = brandService.findByAll();
        return findBrandList.stream()
                .collect(Collectors.toMap(BrandModel::getBrandId, BrandModel::getName));
    }
}
