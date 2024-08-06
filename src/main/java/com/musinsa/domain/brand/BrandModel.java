package com.musinsa.domain.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BrandModel {

    private Long brandId;

    private String name;

    public static BrandModel of(Long id, String name) {
        return BrandModel.builder()
                .brandId(id)
                .name(name)
                .build();
    }
}
