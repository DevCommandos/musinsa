package com.musinsa.infra.brand;

import com.musinsa.domain.brand.BrandModel;
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
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    private String name;

    public static Brand ofBrandModel(BrandModel brandModel) {
        return Brand.builder()
                .id(brandModel.getBrandId())
                .name(brandModel.getName())
                .build();
    }

    public BrandModel toModel() {
        return BrandModel.builder()
                .brandId(this.id)
                .name(this.name)
                .build();
    }
}
