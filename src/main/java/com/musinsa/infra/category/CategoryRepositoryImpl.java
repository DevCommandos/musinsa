package com.musinsa.infra.category;

import com.musinsa.domain.Exception.CustomException;
import com.musinsa.domain.Exception.ExceptionEnum;
import com.musinsa.domain.cache.CacheCategoryMinMax;
import com.musinsa.domain.category.CategoryMinMaxPrice;
import com.musinsa.domain.category.CategoryMinMaxPrice.PriceInfo;
import com.musinsa.domain.category.CategoryMinPrice;
import com.musinsa.domain.category.CategoryMinPrice.ProductInfo;
import com.musinsa.domain.category.CategoryModel;
import com.musinsa.domain.category.CategoryRepository;
import com.musinsa.infra.redis.RedisKey;
import com.musinsa.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final RedisRepository redisRepository;

    @Override
    public CategoryModel save(CategoryModel categoryModel) {
        Category category = Category.ofCategoryModel(categoryModel);
        Category saveCategory = categoryJpaRepository.save(category);
        return saveCategory.toCategoryModel();
    }

    @Override
    public List<CategoryModel> findByAll() {
        List<Category> categoryList = categoryJpaRepository.findAll();

        List<CategoryModel> categoryModels = new ArrayList<>();
        for (Category category : categoryList) {
            CategoryModel categoryModel = category.toCategoryModel();
            categoryModels.add(categoryModel);
        }

        return categoryModels;
    }

    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public CategoryMinPrice getCategoryMinPrices() {
        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        Map<String, Object> cachedData = redisRepository.getAll(redisKey);

        List<ProductInfo> productList = new ArrayList<>();
        int totalPrice = 0;

        for (Map.Entry<String, Object> entry : cachedData.entrySet()) {
            Long categoryId = Long.valueOf(entry.getKey());
            CacheCategoryMinMax cacheCategoryMinMax = (CacheCategoryMinMax) entry.getValue();
            CacheCategoryMinMax.PriceInfo minPriceInfo = cacheCategoryMinMax.getMinMaxPriceInfo().getMin();
            var productInfo = ProductInfo.of(categoryId, minPriceInfo.getBrandId(), minPriceInfo.getPrice());
            productList.add(productInfo);
            totalPrice += minPriceInfo.getPrice();
        }

        return new CategoryMinPrice(totalPrice, productList);
    }

    @Override
    public CategoryModel findByCategoryName(String categoryName) {
        Category category = categoryJpaRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(ExceptionEnum.CATEGORY_NOT_FOUND));
        return CategoryModel.of(category.getId(), category.getName());
    }

    @Override
    public CategoryMinMaxPrice getCategoryMinMaxPrice(Long categoryId) {
        String redisKey = RedisKey.CATEGORY_MIN_MAX.name();
        CacheCategoryMinMax categoryMinMax = (CacheCategoryMinMax) redisRepository.get(redisKey, String.valueOf(categoryId));

        CacheCategoryMinMax.PriceInfo cacheMin = categoryMinMax.getMinMaxPriceInfo().getMin();
        CacheCategoryMinMax.PriceInfo cacheMax = categoryMinMax.getMinMaxPriceInfo().getMax();

        PriceInfo min = new PriceInfo(null, cacheMin.getBrandId(), cacheMin.getPrice());
        PriceInfo max = new PriceInfo(null, cacheMax.getBrandId(), cacheMax.getPrice());

        return new CategoryMinMaxPrice(null, categoryId, List.of(min), List.of(max));
    }
}
