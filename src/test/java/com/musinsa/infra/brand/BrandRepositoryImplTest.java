package com.musinsa.infra.brand;

import com.musinsa.domain.brand.SingleBrandLowerPrice;
import com.musinsa.domain.cache.CacheBrandCategoryMin;
import com.musinsa.domain.cache.CacheBrandCategoryMin.CategoryPriceInfo;
import com.musinsa.infra.redis.RedisKey;
import com.musinsa.infra.redis.RedisRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandRepositoryImplTest {
    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private BrandRepositoryImpl brandRepositoryImpl;

    @Test
    @DisplayName("1.브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드 - 캐시")
    public void 카테고리_상품을_구매할때_최저가격에_판매하는_브랜드() {

        String redisKey = RedisKey.SINGLE_BRAND_MIN.name();
        Map<String, Object> cachedData = new HashMap<>();

        List<CategoryPriceInfo> categoryPriceInfos1 = Arrays.asList(
                new CategoryPriceInfo("1", 10000),
                new CategoryPriceInfo("2", 15000)
        );
        List<CategoryPriceInfo> categoryPriceInfos2 = Arrays.asList(
                new CategoryPriceInfo("1", 20000),
                new CategoryPriceInfo("2", 25000)
        );
        CacheBrandCategoryMin cacheBrandCategoryMin1 = new CacheBrandCategoryMin("1", categoryPriceInfos1);
        CacheBrandCategoryMin cacheBrandCategoryMin2 = new CacheBrandCategoryMin("2", categoryPriceInfos2);
        cachedData.put("1", cacheBrandCategoryMin1);
        cachedData.put("2", cacheBrandCategoryMin2);

        when(redisRepository.getAll(redisKey)).thenReturn(cachedData);

        SingleBrandLowerPrice result = brandRepositoryImpl.getLowestPriceSingleBrand();

        Assertions.assertEquals(1, result.getLowestPrice().getBrandId());
        Assertions.assertEquals(25000, result.getLowestPrice().getTotalAmount());
    }
}