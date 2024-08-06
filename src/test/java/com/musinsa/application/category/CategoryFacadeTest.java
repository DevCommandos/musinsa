package com.musinsa.application.category;

import com.musinsa.application.common.MappingFacade;
import com.musinsa.domain.category.CategoryMinMaxPrice;
import com.musinsa.domain.category.CategoryMinPrice;
import com.musinsa.domain.category.CategoryMinPrice.ProductInfo;
import com.musinsa.domain.category.CategoryModel;
import com.musinsa.domain.category.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryFacadeTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private MappingFacade mappingFacade;

    @InjectMocks
    private CategoryFacade categoryFacade;

    @Test
    @DisplayName("1.카테고리 별 최저가격 상품 조회")
    public void 카테고리_별_최저가격_상품_조회() {

        Map<Long, String> categoryMap = new HashMap<>();
        categoryMap.put(1L, "상의");
        categoryMap.put(2L, "아우터");

        Map<Long, String> brandMap = new HashMap<>();
        brandMap.put(1L, "브랜드A");
        brandMap.put(2L, "브랜드B");

        ProductInfo productInfo1 = ProductInfo.of(1L, 1L, 10000);
        ProductInfo productInfo2 = ProductInfo.of(2L, 2L, 20000);
        List<ProductInfo> productList = List.of(productInfo1, productInfo2);
        CategoryMinPrice categoryMinPrice = new CategoryMinPrice(30000, productList);

        when(mappingFacade.getCategoryMap()).thenReturn(categoryMap);
        when(mappingFacade.getBrandMap()).thenReturn(brandMap);
        when(categoryService.getCategoryMinPrices()).thenReturn(categoryMinPrice);

        // then
        CategoryMinPrice result = categoryFacade.getCategoryMinPrices();

        assertEquals(30000, result.getTotalPrice());
        assertEquals("상의", result.getProductList().get(0).getCategoryName());
        assertEquals("브랜드A", result.getProductList().get(0).getBrandName());
        assertEquals(10000, result.getProductList().get(0).getPrice());
        assertEquals("아우터", result.getProductList().get(1).getCategoryName());
        assertEquals("브랜드B", result.getProductList().get(1).getBrandName());
        assertEquals(20000, result.getProductList().get(1).getPrice());
    }

    @Test
    @DisplayName("2.카테고리명으로 최소 금액,최대 금액 상품 찾기")
    public void 카테고리명_최소금액_최대금액_상품찾기() {
        // given
        String categoryName = "상의";
        Long categoryId = 1L;
        CategoryModel categoryModel = new CategoryModel(categoryId, categoryName);

        Map<Long, String> brandMap = new HashMap<>();
        brandMap.put(1L, "브랜드A");
        brandMap.put(2L, "브랜드B");

        CategoryMinMaxPrice.PriceInfo minPriceInfo = new CategoryMinMaxPrice.PriceInfo(null, 1L, 10000);
        CategoryMinMaxPrice.PriceInfo maxPriceInfo = new CategoryMinMaxPrice.PriceInfo(null, 2L, 20000);
        CategoryMinMaxPrice categoryMinMaxPrice = new CategoryMinMaxPrice(null, categoryId, List.of(minPriceInfo), List.of(maxPriceInfo));

        // when
        when(categoryService.findByCategoryName(categoryName)).thenReturn(categoryModel);
        when(categoryService.getCategoryMinMaxPrice(categoryId)).thenReturn(categoryMinMaxPrice);
        when(mappingFacade.getBrandMap()).thenReturn(brandMap);

        // then
        CategoryMinMaxPrice result = categoryFacade.getCategoryMinMaxPrice(categoryName);

        assertEquals(categoryName, result.getCategoryName());
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("브랜드A", result.getMinPrice().get(0).getBrandName());
        assertEquals(10000, result.getMinPrice().get(0).getPrice());
        assertEquals("브랜드B", result.getMaxPrice().get(0).getBrandName());
        assertEquals(20000, result.getMaxPrice().get(0).getPrice());
    }
}