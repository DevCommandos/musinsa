package com.musinsa.application.product;

import com.musinsa.domain.category.CategoryService;
import com.musinsa.domain.product.ProductModel;
import com.musinsa.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductModel upsert(Long productId, String name, int price, Long categoryId, Long brandId) {
        ProductModel productModel = ProductModel.of(productId, name, price, categoryId, brandId);
        return productService.save(productModel);
    }

    public void delete(Long productId) {
        productService.deleteById(productId);
    }

    //카테고리별 최소금액 상품 찾기
    //캐싱하기
    /*public List<ProductModel> findMinPriceProductByCategory() {
        List<ProductModel> productList = new ArrayList<>();
        List<CategoryModel> categoryList = categoryService.findByAll();
        for (CategoryModel categoryModel : categoryList) {
            ProductModel productModel = productService.findTopCategoryByPriceAsc(categoryModel.getCategoryId());
            productList.add(productModel);
        }

        return productList;
    }*/
}
