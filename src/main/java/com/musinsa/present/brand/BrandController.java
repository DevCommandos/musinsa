package com.musinsa.present.brand;

import com.musinsa.application.brand.BrandFacade;
import com.musinsa.present.response.ResponseDto;
import com.musinsa.domain.brand.BrandModel;
import com.musinsa.domain.brand.SingleBrandLowerPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BrandController {

    private final BrandFacade brandFacade;

    @PostMapping("/brand")
    public ResponseEntity<ResponseDto> saveBrand(@RequestBody @Validated BrandRequestDto requestDto) {
        BrandModel brandModel = brandFacade.save(requestDto.getName());
        ResponseDto success = ResponseDto.success(brandModel);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/brand/{brandId}")
    public ResponseEntity<ResponseDto> updateBrand(@PathVariable Long brandId, @RequestBody @Validated BrandRequestDto requestDto) {
        BrandModel brandModel = brandFacade.update(brandId, requestDto.getName());
        ResponseDto success = ResponseDto.success(brandModel);
        return ResponseEntity.ok(success);
    }

    @DeleteMapping("/brand/{brandId}")
    public ResponseEntity<ResponseDto> deleteBrand(@PathVariable Long brandId) {
        brandFacade.delete(brandId);
        ResponseDto success = ResponseDto.success();
        return ResponseEntity.ok(success);
    }

    @GetMapping("/brand")
    public ResponseEntity<ResponseDto> findBrandAll() {
        List<BrandModel> brandModelList = brandFacade.findByAll();
        ResponseDto success = ResponseDto.success(brandModelList);
        return ResponseEntity.ok(success);
    }

    /**
     * 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 카테고리 조회
     * @return
     */
    @GetMapping("/brand/lowest-price")
    public ResponseEntity<ResponseDto> getLowestPriceSingleBrand() {
        SingleBrandLowerPrice singleBrand = brandFacade.getLowestPriceSingleBrand();
        ResponseDto success = ResponseDto.success(singleBrand);
        return ResponseEntity.ok(success);
    }
}
