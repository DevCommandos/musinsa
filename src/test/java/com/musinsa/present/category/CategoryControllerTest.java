package com.musinsa.present.category;

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
class CategoryControllerTest {

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
    @DisplayName("1. 카테고리 저장 테스트")
    void 카테고리_저장_테스트() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("상의");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("2. 카테고리 수정 테스트")
    void 카테고리_수정_테스트() throws Exception {

        Category save = categoryJpaRepository.save(new Category(null, "상의"));
        Long categoryId = save.getId();

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("하의");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("3. 카테고리 삭제 테스트")
    void 카테고리_삭제_테스트() throws Exception {

        Category save = categoryJpaRepository.save(new Category(null, "상의"));
        Long categoryId = save.getId();

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("옷");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("4. 카테고리 목록 조회")
    void 카테고리_목록_조회() throws Exception {

        categoryJpaRepository.save(new Category(null, "상의"));
        categoryJpaRepository.save(new Category(null, "아우터"));
        categoryJpaRepository.save(new Category(null, "바지"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result[0].name", is("상의")))
                .andExpect(jsonPath("$.result[1].name", is("아우터")))
                .andExpect(jsonPath("$.result[2].name", is("바지")));
    }

    @Test
    @DisplayName("5. 카테고리별 최저가격 상품 조회")
    void 카테고리별_최저가격_상품_조회() throws Exception {

        //카테고리 상의에 100원 200원, 아우터에 1000원 2000원 상품 추가
        //상의 100원, 아우터 1000원 총합 1100 나와야 함.
        Category saveCate1 = categoryJpaRepository.save(new Category(null, "상의"));
        Category saveCate2 = categoryJpaRepository.save(new Category(null, "아우터"));
        Brand saveBrand = brandJpaRepository.save(new Brand(null, "A"));

        productRepository.save(new ProductModel(null,"상품1",100,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품2",200,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품3",1000,saveCate2.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품4",2000,saveCate2.getId(),saveBrand.getId()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/min-prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result.totalPrice", is(1100)));
    }

    @Test
    @DisplayName("6. 카테고리명으로 최소 금액,최대 금액 상품 조회")
    void 카테고리명으로_최소금액_최대금액_상품_조회() throws Exception {

        //카테고리 상의에 100원 200원 300원 400원 500원 상품 추가
        //상의 최소 금액 100원, 최대 금액 500원이 나와야 함.
        Category saveCate1 = categoryJpaRepository.save(new Category(null, "상의"));
        Brand saveBrand = brandJpaRepository.save(new Brand(null, "A"));

        productRepository.save(new ProductModel(null,"상품1",100,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품2",200,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품3",300,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품4",400,saveCate1.getId(),saveBrand.getId()));
        productRepository.save(new ProductModel(null,"상품5",500,saveCate1.getId(),saveBrand.getId()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/상의/min-max-prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result.categoryName", is("상의")))
                .andExpect(jsonPath("$.result.minPrice[0].price", is(100)))
                .andExpect(jsonPath("$.result.maxPrice[0].price", is(500)));
    }
}