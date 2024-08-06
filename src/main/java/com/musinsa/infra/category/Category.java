package com.musinsa.infra.category;

import com.musinsa.domain.category.CategoryModel;
import com.musinsa.infra.brand.Brand;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    public static Category ofCategoryModel(CategoryModel categoryModel) {
        return Category.builder()
                .id(categoryModel.getCategoryId())
                .name(categoryModel.getName())
                .build();
    }

    public CategoryModel toCategoryModel() {
        return CategoryModel.builder()
                .categoryId(this.id)
                .name(this.name)
                .build();
    }
}
