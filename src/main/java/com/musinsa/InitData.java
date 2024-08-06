package com.musinsa;


import com.musinsa.domain.product.ProductModel;
import com.musinsa.infra.brand.Brand;
import com.musinsa.infra.brand.BrandJpaRepository;
import com.musinsa.infra.category.Category;
import com.musinsa.infra.category.CategoryJpaRepository;
import com.musinsa.infra.product.ProductRepositoryImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class InitData {

    private final CategoryJpaRepository categoryJpaRepository;
    private final BrandJpaRepository brandJpaRepository;
    private final ProductRepositoryImpl productRepositoryImpl;

    @PostConstruct
    public void init() {

        log.info("=========기초 데이터 생성==========");

        // 카테고리 생성
        categoryJpaRepository.save(new Category(1L, "상의"));
        categoryJpaRepository.save(new Category(2L, "아우터"));
        categoryJpaRepository.save(new Category(3L, "바지"));
        categoryJpaRepository.save(new Category(4L, "스니커즈"));
        categoryJpaRepository.save(new Category(5L, "가방"));
        categoryJpaRepository.save(new Category(6L, "모자"));
        categoryJpaRepository.save(new Category(7L, "양말"));
        categoryJpaRepository.save(new Category(8L, "액세서리"));

        //브랜드 생성
        createBrand("A");
        createBrand("B");
        createBrand("C");
        createBrand("D");
        createBrand("E");
        createBrand("F");
        createBrand("G");
        createBrand("H");
        createBrand("I");

        //제품생성
        createProduct();
    }

    private void createBrand(String brandName) {
        brandJpaRepository.save(new Brand(null, brandName));
    }

    private void createProduct() {
        int[][] prices = {
                {11200, 5500, 4200, 9000, 2000, 1700, 1800, 2300},
                {10500, 5900, 3800, 9100, 2100, 2000, 2000, 2200},
                {10000, 6200, 3300, 9200, 2200, 1900, 2200, 2100},
                {10100, 5100, 3000, 9500, 2500, 1500, 2400, 2000},
                {10700, 5000, 3800, 9900, 2300, 1800, 2100, 2100},
                {11200, 7200, 4000, 9300, 2100, 1600, 2300, 1900},
                {10500, 5800, 3900, 9000, 2200, 1700, 2100, 2000},
                {10800, 6300, 3100, 9700, 2100, 1600, 2000, 2000},
                {11400, 6700, 3200, 9500, 2400, 1700, 1700, 2400}
        };

        for (long categoryId = 1; categoryId <= 8; categoryId++) {
            for (long brandId = 1; brandId <= 9; brandId++) {
                int price = prices[(int) (brandId - 1)][(int) (categoryId - 1)];
                String productName = "카테고리" + categoryId + " 브랜드" + brandId + " 제품";
                ProductModel productModel = ProductModel.of(null, productName, price, categoryId, brandId);
                productRepositoryImpl.save(productModel);
            }
        }
    }
}
