package com.musinsa.infra.product;

import com.musinsa.domain.cache.CacheBrandCategoryMin;
import com.musinsa.domain.cache.CacheCategoryMinMax;
import com.musinsa.domain.product.ProductModel;
import com.musinsa.infra.redis.RedisKey;
import com.musinsa.infra.redis.RedisRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.musinsa.infra.redis.RedisKey.SINGLE_BRAND_MIN;

@Transactional
@SpringBootTest
class ProductRepositoryImplTest {

    @Autowired
    ProductRepositoryImpl productRepositoryImpl;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    RedisRepository redisRepository;

    @BeforeEach
    void 캐시초기화(){
        redisRepository.clear();
    }


    @Test
    @DisplayName("1.상품 저장 후 카테고리별 캐시 테스트 - 최초 등록")
    void 상품_저장_후_카테고리별_캐시_테스트_최초_등록() {
        Long categoryId = 1L;
        int price = 5000;
        ProductModel productModel = ProductModel.of(null, "상품1", 5000, categoryId, 1L);

        productRepositoryImpl.save(productModel);

        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        String hashKey = String.valueOf(categoryId);
        CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, hashKey);
        int minPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMin().getPrice();
        int maxPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMax().getPrice();

        Assertions.assertEquals(price, minPrice);
        Assertions.assertEquals(price, maxPrice);
    }

    @Test
    @DisplayName("2.상품 저장 후 카테고리별 캐시 테스트 - 캐시 수정")
    void 상품_저장_후_카테고리별_캐시_테스트_캐시_수정() {
        Long categoryId = 1L;
        int price = 5000;
        ProductModel productModel = ProductModel.of(null, "상품1", price, categoryId, 1L);
        productRepositoryImpl.save(productModel);

        int newPrice = 8000;
        ProductModel newProductModel = ProductModel.of(null, "상품2", newPrice, categoryId, 1L);
        productRepositoryImpl.save(newProductModel);

        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        String hashKey = String.valueOf(categoryId);
        CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, hashKey);
        int minPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMin().getPrice();
        int maxPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMax().getPrice();

        Assertions.assertEquals(price, minPrice);
        Assertions.assertEquals(newPrice, maxPrice);
    }

    @Test
    @DisplayName("3.상품 삭제 후 카테고리별 캐시 테스트")
    void 상품_삭제_후_카테고리별_캐시_테스트() {
        Long categoryId = 1L;
        int minPrice = 1000;
        int nextMinPrice = 2000;
        int maxPrice = 30000;

        ProductModel minProduct = ProductModel.of(null, "상품1", minPrice, categoryId, 1L);
        ProductModel nextMinProduct = ProductModel.of(null, "상품2", nextMinPrice, categoryId, 2L);
        ProductModel maxProduct = ProductModel.of(null, "상품3", maxPrice, categoryId, 3L);

        ProductModel saveMinProduct = productRepositoryImpl.save(minProduct);
        productRepositoryImpl.save(nextMinProduct);
        productRepositoryImpl.save(maxProduct);

        productRepositoryImpl.deleteById(saveMinProduct.getProductId());

        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        String categoryKey = String.valueOf(categoryId);

        CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, categoryKey);
        int getMinPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMin().getPrice();
        int getMaxPrice = cacheCategoryMinMax.getMinMaxPriceInfo().getMax().getPrice();

        Assertions.assertEquals(nextMinPrice, getMinPrice);
        Assertions.assertEquals(maxPrice, getMaxPrice);
    }

    @Test
    @DisplayName("4.상품 저장 후 브랜드별 캐시 테스트 - 최초 등록")
    void 상품_저장_후_브랜드별_캐시_테스트_최초_등록() {
        Long brandId = 1L;
        Long categoryId = 1L;
        int price = 5000;
        ProductModel productModel = ProductModel.of(null, "상품1", price, categoryId, brandId);

        productRepositoryImpl.save(productModel);

        String redisKey = SINGLE_BRAND_MIN.name();
        String brandKey = String.valueOf(brandId);
        CacheBrandCategoryMin cacheBrandCategoryMin = (CacheBrandCategoryMin) redisRepository.get(redisKey, brandKey);
        int cachePrice = cacheBrandCategoryMin.getCategoryList().get(0).getPrice();
        String cacheCategory = cacheBrandCategoryMin.getCategoryList().get(0).getCategoryId();

        Assertions.assertEquals(price, cachePrice);
        Assertions.assertEquals(String.valueOf(categoryId), cacheCategory);
    }

    @Test
    @DisplayName("5.상품 저장 후 브랜드별 캐시 테스트 - 캐시 수정")
    void 상품_저장_후_브랜드별_캐시_테스트_캐시_수정() {
        Long brandId = 1L;
        Long categoryId = 1L;
        int price = 5000;
        ProductModel productModel = ProductModel.of(null, "상품1", price, categoryId, brandId);
        ProductModel saveProduct = productRepositoryImpl.save(productModel);

        int newPrice = 4000;
        ProductModel newProductModel = ProductModel.of(saveProduct.getProductId(), "상품2", newPrice, categoryId, brandId);
        productRepositoryImpl.save(newProductModel);

        String redisKey = SINGLE_BRAND_MIN.name();
        String brandKey = String.valueOf(brandId);
        CacheBrandCategoryMin cacheBrandCategoryMin = (CacheBrandCategoryMin) redisRepository.get(redisKey, brandKey);
        int cachePrice = cacheBrandCategoryMin.getCategoryList().get(0).getPrice();

        Assertions.assertEquals(newPrice, cachePrice);
    }

    @Test
    @DisplayName("6.상품 삭제 후 브랜드별 캐시 테스트")
    void 상품_삭제_후_브랜드별_캐시_테스트() {
        Long brandId = 1L;
        Long categoryId = 1L;
        int minPrice = 500;
        int nextMinPrice = 600;
        int maxPrice = 700;

        ProductModel minProduct = ProductModel.of(null, "상품1", minPrice, categoryId, brandId);
        ProductModel nextMinProduct = ProductModel.of(null, "상품2", nextMinPrice, categoryId, brandId);
        ProductModel maxProduct = ProductModel.of(null, "상품3", maxPrice, categoryId, brandId);

        ProductModel savedMinProduct = productRepositoryImpl.save(minProduct);
        productRepositoryImpl.save(nextMinProduct);
        productRepositoryImpl.save(maxProduct);

        productRepositoryImpl.deleteById(savedMinProduct.getProductId());

        String redisKey = SINGLE_BRAND_MIN.name();
        String brandKey = String.valueOf(brandId);
        CacheBrandCategoryMin cacheBrandCategoryMin = (CacheBrandCategoryMin) redisRepository.get(redisKey, brandKey);
        int cachePrice = cacheBrandCategoryMin.getCategoryList().get(0).getPrice();

        Assertions.assertEquals(nextMinPrice, cachePrice);
    }
}