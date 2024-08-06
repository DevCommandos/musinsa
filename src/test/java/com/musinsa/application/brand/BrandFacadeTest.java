package com.musinsa.application.brand;

import com.musinsa.application.common.MappingFacade;
import com.musinsa.domain.brand.BrandService;
import com.musinsa.domain.brand.SingleBrandLowerPrice;
import com.musinsa.domain.brand.SingleBrandLowerPrice.CategoryPrice;
import com.musinsa.domain.brand.SingleBrandLowerPrice.LowestPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandFacadeTest {

    @Mock
    private BrandService brandService;

    @Mock
    private MappingFacade mappingFacade;

    @InjectMocks
    private BrandFacade brandFacade;

    @Test
    @DisplayName("1.단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 카테고리 조회")
    public void 단일_브랜드로_카테고리_상품을_구매할때_최저가격_카테고리_조회() {
        // given
        Map<Long, String> categoryMap = new HashMap<>();
        categoryMap.put(1L, "상의");
        categoryMap.put(2L, "아우터");

        Map<Long, String> brandMap = new HashMap<>();
        brandMap.put(1L, "브랜드A");

        List<CategoryPrice> categoryPrices = List.of(
                CategoryPrice.of("1", 10000)
                ,CategoryPrice.of("2", 20000)
        );

        LowestPrice lowestPrice = new LowestPrice("브랜드A", 1L, categoryPrices, 30000);
        SingleBrandLowerPrice singleBrandLowerPrice = new SingleBrandLowerPrice(lowestPrice);

        // when
        when(mappingFacade.getCategoryMap()).thenReturn(categoryMap);
        when(mappingFacade.getBrandMap()).thenReturn(brandMap);
        when(brandService.getLowestPriceSingleBrand()).thenReturn(singleBrandLowerPrice);

        // then
        SingleBrandLowerPrice result = brandFacade.getLowestPriceSingleBrand();

        assertEquals("브랜드A", result.getLowestPrice().getBrandName());
        assertEquals(1L, result.getLowestPrice().getBrandId());
        assertEquals(30000, result.getLowestPrice().getTotalAmount());

        List<CategoryPrice> categoryList = result.getLowestPrice().getCategoryList();
        Map<Long, String> resultCategoryMap = categoryList.stream()
                .collect(Collectors.toMap(CategoryPrice::getCategoryId, CategoryPrice::getCategoryName));

        assertEquals("상의", resultCategoryMap.get(1L));
        assertEquals("아우터", resultCategoryMap.get(2L));
        assertEquals(10000, categoryList.get(0).getPrice());
        assertEquals(20000, categoryList.get(1).getPrice());
    }

}