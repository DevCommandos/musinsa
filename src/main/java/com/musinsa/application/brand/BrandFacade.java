package com.musinsa.application.brand;

import com.musinsa.application.common.MappingFacade;
import com.musinsa.domain.brand.BrandModel;
import com.musinsa.domain.brand.BrandService;
import com.musinsa.domain.brand.SingleBrandLowerPrice;
import com.musinsa.domain.brand.SingleBrandLowerPrice.CategoryPrice;
import com.musinsa.domain.brand.SingleBrandLowerPrice.LowestPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;
    private final MappingFacade mappingFacade;

    public BrandModel save(String name) {
        BrandModel brandModel = BrandModel.of(null, name);
        return brandService.save(brandModel);
    }

    public BrandModel update(Long id, String name) {
        BrandModel brandModel = BrandModel.of(id, name);
        return brandService.save(brandModel);
    }

    public void delete(Long id) {
        brandService.deleteById(id);
    }

    public List<BrandModel> findByAll() {
        return brandService.findByAll();
    }

    //단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 카테고리 조회
    public SingleBrandLowerPrice getLowestPriceSingleBrand() {
        Map<Long, String> categoryMap = mappingFacade.getCategoryMap();
        Map<Long, String> brandMap = mappingFacade.getBrandMap();

        SingleBrandLowerPrice lowestBrand = brandService.getLowestPriceSingleBrand();
        LowestPrice lowestPrice = lowestBrand.getLowestPrice();

        //브랜드명 이름 추가
        List<CategoryPrice> categoryPriceList = lowestPrice.getCategoryList().stream()
                .map(category -> {
                    String categoryName = categoryMap.get(category.getCategoryId());
                    return new CategoryPrice(categoryName,category.getCategoryId(),category.getPrice());
                })
                .collect(Collectors.toList());

        LowestPrice lowPrice = new LowestPrice(brandMap.get(Long.valueOf(lowestPrice.getBrandId()))
                , lowestPrice.getBrandId()
                , categoryPriceList
                , lowestPrice.getTotalAmount()
        );

        return new SingleBrandLowerPrice(lowPrice);
    }
}
