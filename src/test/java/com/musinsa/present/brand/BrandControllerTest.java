package com.musinsa.present.brand;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.domain.product.ProductModel;
import com.musinsa.infra.brand.Brand;
import com.musinsa.infra.brand.BrandJpaRepository;
import com.musinsa.infra.category.Category;
import com.musinsa.infra.category.CategoryJpaRepository;
import com.musinsa.infra.product.ProductRepositoryImpl;
import com.musinsa.infra.redis.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CategoryJpaRepository categoryJpaRepository;

    @Autowired
    BrandJpaRepository brandJpaRepository;

    @Autowired
    ProductRepositoryImpl productRepository;

    @Autowired
    RedisRepository redisRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void clear(){
        redisRepository.clear();
    }

    @Test
    @DisplayName("1. 브랜드 저장 테스트")
    void 브랜드_저장_테스트() throws Exception {
        BrandRequestDto requestDto = new BrandRequestDto();
        requestDto.setName("브랜드A");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("2. 브랜드 수정 테스트")
    void 브랜드_수정_테스트() throws Exception {

        Brand savedBrand = brandJpaRepository.save(new Brand(null, "브랜드A"));
        Long brandId = savedBrand.getId();

        BrandRequestDto requestDto = new BrandRequestDto();
        requestDto.setName("NEW브랜드A");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand/" + brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result.brandId", is(brandId.intValue())))
                .andExpect(jsonPath("$.result.name", is("NEW브랜드A")));
    }

    @Test
    @DisplayName("3. 브랜드 삭제 테스트")
    void 브랜드_삭제_테스트() throws Exception {

        Brand savedBrand = brandJpaRepository.save(new Brand(null, "브랜드A"));
        Long brandId = savedBrand.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brand/" + brandId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("4. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 카테고리 조회")
    void 단일_브랜드로_모든_카테고리_상품을_구매할_때_최저가격_카테고리_조회() throws Exception {
        Category saveCate1 = categoryJpaRepository.save(new Category(null, "상의"));
        Category saveCate2 = categoryJpaRepository.save(new Category(null, "아우터"));

        Brand saveBrand1 = brandJpaRepository.save(new Brand(null, "브랜드A"));
        Brand saveBrand2 = brandJpaRepository.save(new Brand(null, "브랜드B"));

        //상의 카테고리, 브랜드A에 100원 상품 추가
        productRepository.save(new ProductModel(null, "상품1", 100, saveCate1.getId(), saveBrand1.getId()));
        //아우터 카테고리, 브랜드A에 200원 상품 추가
        productRepository.save(new ProductModel(null, "상품2", 200, saveCate2.getId(), saveBrand1.getId()));
        //상의 카테고리, 브랜드B에 300원 상품 추가
        productRepository.save(new ProductModel(null, "상품3", 300, saveCate1.getId(), saveBrand2.getId()));
        //아우터 카테고리, 브랜드B에 400원 상품 추가
        productRepository.save(new ProductModel(null, "상품4", 400, saveCate2.getId(), saveBrand2.getId()));

        //브랜드A 총합이 300원이므로 브랜드A, 총합 300원 정보가 나와야 함.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/lowest-price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result.lowestPrice.brandName", is("브랜드A")))
                .andExpect(jsonPath("$.result.lowestPrice.totalAmount", is(300)));
    }
}