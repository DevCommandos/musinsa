package com.musinsa.present.category;

import com.musinsa.application.category.CategoryFacade;
import com.musinsa.present.response.ResponseDto;
import com.musinsa.domain.category.CategoryMinMaxPrice;
import com.musinsa.domain.category.CategoryMinPrice;
import com.musinsa.domain.category.CategoryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryFacade categoryFacade;

    @PostMapping("/category")
    public ResponseEntity<ResponseDto> saveCategory(@RequestBody @Validated CategoryRequestDto requestDto) {
        CategoryModel categoryModel = categoryFacade.save(requestDto.getName());
        ResponseDto success = ResponseDto.success(categoryModel);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDto> updateCategory(@PathVariable Long categoryId, @RequestBody @Validated CategoryRequestDto requestDto) {
        CategoryModel categoryModel = categoryFacade.update(categoryId, requestDto.getName());
        ResponseDto success = ResponseDto.success(categoryModel);
        return ResponseEntity.ok(success);
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDto> deleteCategory(@PathVariable Long categoryId) {
        categoryFacade.delete(categoryId);
        ResponseDto success = ResponseDto.success();
        return ResponseEntity.ok(success);
    }

    @GetMapping(value = "/category")
    public ResponseEntity<ResponseDto> findCategoryAll() {
        List<CategoryModel> categoryModelList = categoryFacade.findByAll();
        ResponseDto success = ResponseDto.success(categoryModelList);
        return ResponseEntity.ok(success);
    }

    /**
     * 카테고리 별 최저가격 상품 조회
     */
    @GetMapping("/category/min-prices")
    public ResponseEntity<ResponseDto> getCategoryMinPrices() {
        CategoryMinPrice response = categoryFacade.getCategoryMinPrices();
        ResponseDto success = ResponseDto.success(response);
        return ResponseEntity.ok(success);
    }

    /**
     * 카테고리명으로 최소 금액,최대 금액 상품 조회
     */
    @GetMapping("/category/{categoryName}/min-max-prices")
    public ResponseEntity<ResponseDto> getCategoryMinMaxPrice(@PathVariable String categoryName) {
        CategoryMinMaxPrice response = categoryFacade.getCategoryMinMaxPrice(categoryName);
        ResponseDto responseDto = ResponseDto.success(response);
        return ResponseEntity.ok(responseDto);

    }
}