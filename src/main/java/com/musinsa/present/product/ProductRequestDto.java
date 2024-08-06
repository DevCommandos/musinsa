package com.musinsa.present.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {

    private String name;

    private int price;

    private Long categoryId;

    private Long brandId;
}
