package com.musinsa.infra.product;

import com.musinsa.infra.brand.Brand;
import com.musinsa.infra.brand.BrandJpaRepository;
import com.musinsa.infra.category.Category;
import com.musinsa.infra.category.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class ProductJpaRepositoryTest {

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    CategoryJpaRepository categoryJpaRepository;

    @Autowired
    BrandJpaRepository brandJpaRepository;

    @Test
    @DisplayName("1. 카테고리별 최소금액 조회하기")
    void 카테고리별_최소금액_조회하기() {

        Long categoryId = 1L;

        Category category = new Category(categoryId, "상의");
        categoryJpaRepository.save(category);

        Brand brand = new Brand(null, "A");
        Brand brand2 = new Brand(null, "B");
        brandJpaRepository.save(brand);
        brandJpaRepository.save(brand2);

        Product product = new Product(null, "상품1", 1000, 1L, categoryId); //최소
        Product product2 = new Product(null, "상품2", 5000, 2L, categoryId);
        productJpaRepository.save(product);
        productJpaRepository.save(product2);

        Optional<ProductProjection> productModel = productJpaRepository.findTopCategoryByPriceAsc(categoryId);

        Assertions.assertTrue(productModel.isPresent());
        Assertions.assertEquals("상품1", productModel.get().getName());
        Assertions.assertEquals(1000, productModel.get().getPrice());
    }

    @Test
    @DisplayName("2. 상품 저장 후 캐싱 테스트")
    void 상품_저장_후_캐싱_테스트() {

        Long categoryId = 1L;

        Category category = new Category(categoryId, "상의");
        categoryJpaRepository.save(category);

        Brand brand = new Brand(null, "A");
        Brand brand2 = new Brand(null, "B");
        brandJpaRepository.save(brand);
        brandJpaRepository.save(brand2);

        Product product = new Product(null, "상품1", 1000, 1L, categoryId); //최소
        Product product2 = new Product(null, "상품2", 5000, 2L, categoryId);
        productJpaRepository.save(product);
        productJpaRepository.save(product2);

        Optional<ProductProjection> productModel = productJpaRepository.findTopCategoryByPriceAsc(categoryId);

        Assertions.assertTrue(productModel.isPresent());
        Assertions.assertEquals("상품1", productModel.get().getName());
        Assertions.assertEquals(1000, productModel.get().getPrice());
    }
}