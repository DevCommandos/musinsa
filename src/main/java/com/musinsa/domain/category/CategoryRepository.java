package com.musinsa.domain.category;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository {

    CategoryModel save(CategoryModel categoryModel);

    List<CategoryModel> findByAll();

    void deleteById(Long id);

    CategoryMinPrice getCategoryMinPrices();

    CategoryModel findByCategoryName(String categoryName);

    CategoryMinMaxPrice getCategoryMinMaxPrice(Long categoryId);
}
