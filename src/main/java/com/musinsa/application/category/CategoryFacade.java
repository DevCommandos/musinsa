package com.musinsa.application.category;

import com.musinsa.application.common.MappingFacade;
import com.musinsa.domain.brand.BrandService;
import com.musinsa.domain.category.CategoryMinMaxPrice;
import com.musinsa.domain.category.CategoryMinMaxPrice.PriceInfo;
import com.musinsa.domain.category.CategoryMinPrice;
import com.musinsa.domain.category.CategoryModel;
import com.musinsa.domain.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;
    private final MappingFacade mappingFacade;

    public List<CategoryModel> findByAll() {
        return categoryService.findByAll();
    }

    public CategoryModel save(String name) {
        CategoryModel categoryModel = CategoryModel.of(null, name);
        return categoryService.save(categoryModel);
    }

    public CategoryModel update(Long categoryId, String name) {
        CategoryModel categoryModel = CategoryModel.of(categoryId, name);
        return categoryService.save(categoryModel);
    }

    public void delete(Long id) {
        categoryService.delete(id);
    }

    //카테고리 별 최저가격 상품 조회
    public CategoryMinPrice getCategoryMinPrices() {

        Map<Long, String> categoryMap = mappingFacade.getCategoryMap();
        Map<Long, String> brandMap = mappingFacade.getBrandMap();

        List<CategoryMinPrice.ProductInfo> productList = new ArrayList<>();

        CategoryMinPrice minPrice = categoryService.getCategoryMinPrices();

        //카테고리명, 브랜드명 추가
        for (var product : minPrice.getProductList()) {
            String categoryName = categoryMap.get(product.getCategoryId());
            String brandName = brandMap.get(product.getBrandId());

            var productInfo = new CategoryMinPrice.ProductInfo(product.getCategoryId()
                    , product.getCategoryId()
                    , categoryName
                    , brandName
                    , product.getPrice());

            productList.add(productInfo);
        }
        return new CategoryMinPrice(minPrice.getTotalPrice(), productList);
    }

    //입력받은 카테고리명의 최소 금액,최대 금액 상품 찾기
    public CategoryMinMaxPrice getCategoryMinMaxPrice(String categoryName) {
        CategoryModel findCategory = categoryService.findByCategoryName(categoryName);
        Long categoryId = findCategory.getCategoryId();
        CategoryMinMaxPrice categoryMinMaxPrice = categoryService.getCategoryMinMaxPrice(categoryId);
        Map<Long, String> brandMap = mappingFacade.getBrandMap();

        //브랜드명 추가
        List<PriceInfo> minPrice = categoryMinMaxPrice.getMinPrice().stream()
                .map(priceInfo -> {
                    String brandName = brandMap.get(priceInfo.getBrandId());
                    return new PriceInfo(brandName, priceInfo.getBrandId(), priceInfo.getPrice());
                })
                .collect(Collectors.toList());

        //브랜드명 추가
        List<PriceInfo> maxPrice = categoryMinMaxPrice.getMaxPrice().stream()
                .map(priceInfo -> {
                    String brandName = brandMap.get(priceInfo.getBrandId());
                    return new PriceInfo(brandName, priceInfo.getBrandId(), priceInfo.getPrice());
                })
                .collect(Collectors.toList());

        return new CategoryMinMaxPrice(categoryName,categoryId,minPrice,maxPrice);
    }
}
