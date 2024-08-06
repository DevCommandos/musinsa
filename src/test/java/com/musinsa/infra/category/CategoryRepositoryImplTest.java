package com.musinsa.infra.category;

import com.musinsa.domain.cache.CacheCategoryMinMax;
import com.musinsa.domain.cache.CacheCategoryMinMax.MinMaxPriceInfo;
import com.musinsa.domain.cache.CacheCategoryMinMax.PriceInfo;
import com.musinsa.domain.category.CategoryMinMaxPrice;
import com.musinsa.domain.category.CategoryMinPrice;
import com.musinsa.infra.redis.RedisKey;
import com.musinsa.infra.redis.RedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryImplTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private CategoryRepositoryImpl categoryRepositoryImpl;

    @Test
    @DisplayName("1.카테고리 별 최저가격 상품 - 캐시")
    public void 카테고리_별_최저가격_상품_캐시() {
        // given
        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        Map<String, Object> cachedData = new HashMap<>();
        CacheCategoryMinMax cacheCategoryMinMax1 = new CacheCategoryMinMax("1", new MinMaxPriceInfo(
                new PriceInfo(1L, 10000),
                new PriceInfo(2L, 20000)
        ));
        CacheCategoryMinMax cacheCategoryMinMax2 = new CacheCategoryMinMax("2", new MinMaxPriceInfo(
                new PriceInfo(3L, 15000),
                new PriceInfo(4L, 25000)
        ));
        cachedData.put("1", cacheCategoryMinMax1);
        cachedData.put("2", cacheCategoryMinMax2);

        // when
        when(redisRepository.getAll(redisKey)).thenReturn(cachedData);

        // then
        CategoryMinPrice result = categoryRepositoryImpl.getCategoryMinPrices();

        assertEquals(25000, result.getTotalPrice());
        assertEquals(2, result.getProductList().size());
    }

    @Test
    @DisplayName("2.카테고리명으로 최소 금액,최대 금액 상품 찾기 - 캐시")
    public void 카테고리명으로_최소금액_최대금액_상품_캐시() {
        // given
        Long categoryId = 1L;
        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        CacheCategoryMinMax cacheCategoryMinMax = new CacheCategoryMinMax("1", new MinMaxPriceInfo(
                new PriceInfo(1L, 10000),
                new PriceInfo(2L, 20000)
        ));

        // when
        when(redisRepository.get(redisKey, String.valueOf(categoryId))).thenReturn(cacheCategoryMinMax);

        // then
        CategoryMinMaxPrice result = categoryRepositoryImpl.getCategoryMinMaxPrice(categoryId);

        assertEquals(categoryId, result.getCategoryId());
        assertEquals(1, result.getMinPrice().size());
        assertEquals(1, result.getMaxPrice().size());
        assertEquals(10000, result.getMinPrice().get(0).getPrice());
        assertEquals(20000, result.getMaxPrice().get(0).getPrice());
    }
}