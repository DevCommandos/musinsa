package com.musinsa.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryModel> findByAll(){
        return categoryRepository.findByAll();
    }

    public CategoryModel save(CategoryModel categoryModel) {
        return categoryRepository.save(categoryModel);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryMinPrice getCategoryMinPrices() {
        return categoryRepository.getCategoryMinPrices();
    }

    public CategoryModel findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    public CategoryMinMaxPrice getCategoryMinMaxPrice(Long categoryId) {
        return categoryRepository.getCategoryMinMaxPrice(categoryId);
    }
}
