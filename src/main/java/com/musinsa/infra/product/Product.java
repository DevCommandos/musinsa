package com.musinsa.infra.product;

import com.musinsa.domain.product.ProductModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private int price;

    private Long brandId;

    private Long categoryId;

    public static Product ofProductModel(ProductModel productModel) {
        return Product.builder()
                .id(productModel.getProductId())
                .name(productModel.getName())
                .price(productModel.getPrice())
                .brandId(productModel.getBrandId())
                .categoryId(productModel.getCategoryId())
                .build();
    }

    public ProductModel toModel() {
        return ProductModel.builder()
                .productId(this.getId())
                .name(this.getName())
                .price(this.getPrice())
                .brandId(this.getBrandId())
                .categoryId(this.getCategoryId())
                .build();
    }
}
