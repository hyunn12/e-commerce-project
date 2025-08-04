package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandInfo {

    private Long id;
    private String name;
    private String description;

    public static BrandInfo from(Brand brand) {
        return new BrandInfo(
                brand.getId(),
                brand.getName(),
                brand.getDescription()
        );
    }
}
