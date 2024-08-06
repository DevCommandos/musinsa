package com.musinsa.infra.brand;

import com.musinsa.domain.brand.BrandModel;
import com.musinsa.domain.brand.BrandRepository;
import com.musinsa.domain.brand.SingleBrandLowerPrice.CategoryPrice;
import com.musinsa.domain.brand.SingleBrandLowerPrice.LowestPrice;
import com.musinsa.domain.cache.CacheBrandCategoryMin;
import com.musinsa.domain.brand.SingleBrandLowerPrice;
import com.musinsa.infra.redis.RedisKey;
import com.musinsa.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandJpaRepository brandJpaRepository;
    private final RedisRepository redisRepository;

    @Override
    public BrandModel save(BrandModel brandModel) {
        Brand brand = Brand.ofBrandModel(brandModel);
        Brand saveBrand = brandJpaRepository.save(brand);
        return saveBrand.toModel();
    }

    @Override
    public void deleteById(Long id) {
        brandJpaRepository.deleteById(id);
    }

    @Override
    public List<BrandModel> findByAll() {
        List<Brand> brandList = brandJpaRepository.findAll();
        List<BrandModel> brandModels = new ArrayList<>();
        for (Brand brand : brandList) {
            brandModels.add(brand.toModel());
        }
        return brandModels;
    }

    @Override
    public SingleBrandLowerPrice getLowestPriceSingleBrand() {
        String redisKey = RedisKey.SINGLE_BRAND_MIN.name();
        Map<String, Object> cachedData = redisRepository.getAll(redisKey);

        String minBrandId = null;
        int minPrice = 0;

        // 총 금액이 가장 적은 브랜드 찾기
        for (Map.Entry<String, Object> entry : cachedData.entrySet()) {
            CacheBrandCategoryMin cacheBrandCategoryMin = (CacheBrandCategoryMin) entry.getValue();
            int totalAmount = 0;
            for (var categoryPriceInfo : cacheBrandCategoryMin.getCategoryList()) {
                totalAmount += categoryPriceInfo.getPrice();
            }
            if (minBrandId == null || totalAmount < minPrice) {
                minBrandId = cacheBrandCategoryMin.getBrand();
                minPrice = totalAmount;
            }
        }

        // 객체 생성
        CacheBrandCategoryMin categoryMin = (CacheBrandCategoryMin) cachedData.get(minBrandId);
        List<CategoryPrice> categoryPrices = new ArrayList<>();

        for (var categoryPriceInfo : categoryMin.getCategoryList()) {
            CategoryPrice categoryPrice = CategoryPrice.of(categoryPriceInfo.getCategoryId(), categoryPriceInfo.getPrice());
            categoryPrices.add(categoryPrice);
        }

        LowestPrice lowestPrice = LowestPrice.of(minBrandId, categoryPrices, minPrice);

        return new SingleBrandLowerPrice(lowestPrice);
    }
}
