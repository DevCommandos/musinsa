package com.musinsa.domain.product;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository {
    ProductModel save(ProductModel productModel);
    void deleteById(Long id);
    Optional<ProductModel> findTopCategoryByPriceAsc(Long categoryId);
}
