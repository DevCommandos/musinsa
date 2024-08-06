package com.musinsa.infra.product;

import com.musinsa.domain.Exception.CustomException;
import com.musinsa.domain.cache.CacheBrandCategoryMin;
import com.musinsa.domain.cache.CacheCategoryMinMax;
import com.musinsa.domain.product.ProductModel;
import com.musinsa.domain.product.ProductRepository;
import com.musinsa.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

import static com.musinsa.domain.Exception.ExceptionEnum.PRODUCT_NOT_FOUND;
import static com.musinsa.infra.redis.RedisKey.CATEGORY_MIN_MAX;
import static com.musinsa.infra.redis.RedisKey.SINGLE_BRAND_MIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final RedisRepository redisRepository;

    @Override
    public ProductModel save(ProductModel productModel) {
        Product product = Product.ofProductModel(productModel);
        Product savedProduct = productJpaRepository.save(product);

        //캐시 변경
        updateCategoryMinMaxCache(productModel);
        //캐시 변경
        updateSingleBrandMinCache(productModel);

        return savedProduct.toModel();
    }

    @Override
    public void deleteById(Long id) {

        Product product = productJpaRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        productJpaRepository.deleteById(id);
        // 캐시 삭제 후 대체
        updateCategoryMinMaxCacheDelete(product);
        // 캐시 삭제 후 대체
        updateSingleBrandMinCacheDelete(product);
    }

    private void updateSingleBrandMinCache(ProductModel productModel) {
        String redisKey = SINGLE_BRAND_MIN.name();
        String brandKey = String.valueOf(productModel.getBrandId());

        String productBrandId = String.valueOf(productModel.getBrandId());
        String productCategoryId = String.valueOf(productModel.getCategoryId());
        int productPrice = productModel.getPrice();

        CacheBrandCategoryMin cache = (CacheBrandCategoryMin) redisRepository.get(redisKey, brandKey);
        if (cache == null) {
            cache = new CacheBrandCategoryMin(productBrandId, new ArrayList<>());
            cache.getCategoryList()
                    .add(new CacheBrandCategoryMin.CategoryPriceInfo(productCategoryId, productPrice));
            redisRepository.put(redisKey, brandKey, cache);
        } else {
            var categoryPriceInfoList = cache.getCategoryList();
            boolean found = false;

            for (var categoryPriceInfo : categoryPriceInfoList) {
                if (categoryPriceInfo.getCategoryId().equals(productCategoryId)) {
                    if (productPrice < categoryPriceInfo.getPrice()) {
                        categoryPriceInfo.setPrice(productPrice);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                categoryPriceInfoList.add(new CacheBrandCategoryMin.CategoryPriceInfo(productCategoryId, productModel.getPrice()));
            }

            redisRepository.put(redisKey, brandKey, cache);
        }
    }

    private void updateCategoryMinMaxCache(ProductModel productModel) {
        String redisKey = CATEGORY_MIN_MAX.name();
        String categoryKey = String.valueOf(productModel.getCategoryId());

        // 캐시에 데이터가 없으면 새로 생성
        CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, categoryKey);
        if (cacheCategoryMinMax == null) {
            var priceInfo = new CacheCategoryMinMax.PriceInfo(productModel.getBrandId(), productModel.getPrice());
            var minMaxPriceInfo = new CacheCategoryMinMax.MinMaxPriceInfo(priceInfo, priceInfo); // 최소 금액 최대 금액 동일하게 셋팅
            cacheCategoryMinMax = new CacheCategoryMinMax(categoryKey, minMaxPriceInfo);
            redisRepository.put(redisKey, categoryKey, cacheCategoryMinMax);
            log.info("cached!!!! redisKey : {}, categoryKey : {}, categoryMinMax : {}", redisKey, categoryKey, cacheCategoryMinMax);
        } else {
            // 기존 캐시에 있는 데이터를 업데이트

            var minMaxPriceInfo = cacheCategoryMinMax.getMinMaxPriceInfo();

            // 최소 금액일 경우 캐시 업데이트
            if (productModel.getPrice() < minMaxPriceInfo.getMin().getPrice()) {
                minMaxPriceInfo.setMin(new CacheCategoryMinMax.PriceInfo(productModel.getBrandId(), productModel.getPrice()));
            }

            // 최대 금액일 경우 캐시 업데이트
            if (productModel.getPrice() > minMaxPriceInfo.getMax().getPrice()) {
                minMaxPriceInfo.setMax(new CacheCategoryMinMax.PriceInfo(productModel.getBrandId(), productModel.getPrice()));
            }
            cacheCategoryMinMax.setMinMaxPriceInfo(minMaxPriceInfo);
            redisRepository.put(redisKey, categoryKey, cacheCategoryMinMax);
            log.info("cached!!!! redisKey : {}, categoryKey : {}, categoryMinMax : {}", redisKey, categoryKey, cacheCategoryMinMax);
        }
    }



    private void updateSingleBrandMinCacheDelete(Product product) {
        String redisKey = SINGLE_BRAND_MIN.name();
        String brandKey = String.valueOf(product.getBrandId());

        String productCategoryId = String.valueOf(product.getCategoryId());
        int productPrice = product.getPrice();

        CacheBrandCategoryMin cacheBrandCategoryMin = (CacheBrandCategoryMin) redisRepository.get(redisKey, brandKey);

        var categoryPriceInfoList = cacheBrandCategoryMin.getCategoryList();

        boolean removed = categoryPriceInfoList.removeIf(cache ->
                cache.getCategoryId().equals(productCategoryId) && cache.getPrice() == productPrice);

        if (removed) {
            Optional<ProductProjection> newMinProduct = productJpaRepository.findTopCategoryByPriceAsc(product.getCategoryId());
            newMinProduct.ifPresent(productProjection ->
                    categoryPriceInfoList.add(new CacheBrandCategoryMin.CategoryPriceInfo(productCategoryId, productProjection.getPrice()))
            );
        }

        if (categoryPriceInfoList.isEmpty()) {
            redisRepository.delete(redisKey, brandKey);
        } else {
            redisRepository.put(redisKey, brandKey, cacheBrandCategoryMin);
        }
    }

    private void updateCategoryMinMaxCacheDelete(Product product) {

        String redisKey = CATEGORY_MIN_MAX.name();
        String categoryKey = String.valueOf(product.getCategoryId());

        // 캐시에서 해당 카테고리의 최소/최대 금액 정보 조회
        CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, categoryKey);
        var minMaxPriceInfo = cacheCategoryMinMax.getMinMaxPriceInfo();

        // 삭제된 상품이 최소 금액이었을 경우
        if (product.getPrice() == minMaxPriceInfo.getMin().getPrice()) {
            //최소 금액을 찾아서 업데이트
            Optional<ProductProjection> productPriceAsc = productJpaRepository.findTopCategoryByPriceAsc(product.getCategoryId());
            if (productPriceAsc.isPresent()) { // 그 다음 최소 금액 상품이 존재
                ProductProjection newMinProduct = productPriceAsc.get();
                minMaxPriceInfo.setMin(new CacheCategoryMinMax.PriceInfo(newMinProduct.getBrandId(), newMinProduct.getPrice()));
                log.info("cached!!!! redisKey : {}, categoryKey : {}, categoryMinMax : {}", redisKey, categoryKey, cacheCategoryMinMax);
            } else { // 상품이 완전히 삭제됨 => 캐시에서 제거
                redisRepository.delete(redisKey, categoryKey);
                log.info("Cache Delete!!");
            }
        }
        // 삭제된 상품이 최대 금액이었을 경우
        if (product.getPrice() == minMaxPriceInfo.getMax().getPrice()) {
            //최대 금액을 찾아서 업데이트
            Optional<ProductProjection> productPriceDesc = productJpaRepository.findTopCategoryByPriceDesc(product.getCategoryId());
            if (productPriceDesc.isPresent()) { // 그 다음 최대 금액 상품이 존재
                ProductProjection newMaxProduct = productPriceDesc.get();
                minMaxPriceInfo.setMax(new CacheCategoryMinMax.PriceInfo(newMaxProduct.getBrandId(), newMaxProduct.getPrice()));
                log.info("cached!!!! redisKey : {}, categoryKey : {}, categoryMinMax : {}", redisKey, categoryKey, cacheCategoryMinMax);
            } else { // 상품이 완전히 삭제됨 => 캐시에서 제거
                redisRepository.delete(redisKey, categoryKey);
                log.info("Cache Delete!!");
            }
        }
    }

    @Override
    public Optional<ProductModel> findTopCategoryByPriceAsc(Long categoryId) {
        Optional<ProductProjection> topCategoryByPriceAsc = productJpaRepository.findTopCategoryByPriceAsc(categoryId);

        return topCategoryByPriceAsc.map(p ->
                ProductModel.of(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getCategoryId(),
                        p.getBrandId()
                )
        );
    }
}
