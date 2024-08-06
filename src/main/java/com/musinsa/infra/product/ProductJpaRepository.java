package com.musinsa.infra.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT product_id, name, price, category_id, brand_id " +
            "FROM Product " +
            "WHERE category_id = :categoryId " +
            "ORDER BY price ASC LIMIT 1 ", nativeQuery = true)
    Optional<ProductProjection> findTopCategoryByPriceAsc(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT product_id, name, price, category_id, brand_id " +
            "FROM Product " +
            "WHERE category_id = :categoryId " +
            "ORDER BY price DESC LIMIT 1 ", nativeQuery = true)
    Optional<ProductProjection> findTopCategoryByPriceDesc(@Param("categoryId") Long categoryId);
}

