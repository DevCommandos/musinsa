package com.musinsa.present.product;

import com.musinsa.application.product.ProductFacade;
import com.musinsa.present.response.ResponseDto;
import com.musinsa.domain.product.ProductModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @PostMapping("/product")
    public ResponseEntity<ResponseDto> saveProduct(@RequestBody @Validated ProductRequestDto requestDto) {
        ProductModel productModel = productFacade.upsert(null
                , requestDto.getName()
                , requestDto.getPrice()
                , requestDto.getCategoryId()
                , requestDto.getBrandId());
        ResponseDto success = ResponseDto.success(productModel);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ResponseDto> updateProduct(@PathVariable Long productId, @RequestBody @Validated ProductRequestDto requestDto) {
        ProductModel productModel = productFacade.upsert(productId, requestDto.getName(), requestDto.getPrice(), requestDto.getCategoryId(), requestDto.getBrandId());
        ResponseDto success = ResponseDto.success(productModel);
        return ResponseEntity.ok(success);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ResponseDto> deleteProduct(@PathVariable Long productId) {
        productFacade.delete(productId);
        ResponseDto success = ResponseDto.success();
        return ResponseEntity.ok(success);
    }
}
