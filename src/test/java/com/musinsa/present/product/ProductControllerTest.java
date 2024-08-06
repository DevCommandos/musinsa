package com.musinsa.present.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.domain.product.ProductModel;
import com.musinsa.infra.brand.BrandJpaRepository;
import com.musinsa.infra.category.CategoryJpaRepository;
import com.musinsa.infra.product.Product;
import com.musinsa.infra.product.ProductJpaRepository;
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
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CategoryJpaRepository categoryJpaRepository;

    @Autowired
    BrandJpaRepository brandJpaRepository;

    @Autowired
    ProductRepositoryImpl productRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    RedisRepository redisRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void clear(){
        redisRepository.clear();
    }

    @Test
    @DisplayName("1. 상품 저장 테스트")
    void 상품_저장_테스트() throws Exception {
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("상품1");
        requestDto.setPrice(1000);
        requestDto.setCategoryId(1L);
        requestDto.setBrandId(1L);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // 응답 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }

    @Test
    @DisplayName("2. 상품 수정 테스트")
    void 상품_수정_테스트() throws Exception {

        Product product = new Product(null,"상품1",500,1L,1L);

        Product saveProduct = productJpaRepository.save(product);

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("상품2");
        requestDto.setPrice(2000);
        requestDto.setCategoryId(1L);
        requestDto.setBrandId(1L);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // 응답 검증
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/" + saveProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")))
                .andExpect(jsonPath("$.result.name", is("상품2")))
                .andExpect(jsonPath("$.result.price", is(2000)));
    }

    @Test
    @DisplayName("3. 상품 삭제 테스트")
    void 상품_삭제_테스트() throws Exception {
        ProductModel productModel = productRepository.save(new ProductModel(null, "상품1", 100, 1L, 1L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product/" + productModel.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("0000")))
                .andExpect(jsonPath("$.message", is("요청 성공")));
    }
}