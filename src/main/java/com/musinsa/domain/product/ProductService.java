package com.musinsa.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductModel save(ProductModel productModel) {
        return productRepository.save(productModel);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    /*public ProductModel findTopCategoryByPriceAsc(Long categoryId) {
        Optional<ProductModel> productModel = productRepository.findTopCategoryByPriceAsc(categoryId);
        return productModel.orElseThrow(() -> new CustomException(NO_PRODUCTS_IN_CATEGORY));
    }*/
}
