package com.musinsa.domain.brand;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository {

    BrandModel save(BrandModel brandModel);

    void deleteById(Long id);

    List<BrandModel> findByAll();

    SingleBrandLowerPrice getLowestPriceSingleBrand();
}
