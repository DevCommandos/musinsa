package com.musinsa.domain.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandModel save(BrandModel brandModel) {
        return brandRepository.save(brandModel);
    }

    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }

    public List<BrandModel> findByAll() {
        return brandRepository.findByAll();
    }

    public SingleBrandLowerPrice getLowestPriceSingleBrand() {
        return brandRepository.getLowestPriceSingleBrand();
    }
}
