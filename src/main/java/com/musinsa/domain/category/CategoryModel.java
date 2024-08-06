package com.musinsa.domain.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryModel {

    private Long categoryId;

    private String name;

    public static CategoryModel of(Long id, String name) {
        return CategoryModel.builder()
                .categoryId(id)
                .name(name)
                .build();
    }
}
