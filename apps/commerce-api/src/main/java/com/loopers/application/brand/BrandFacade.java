package com.loopers.application.brand;

import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;

    public BrandInfo getDetail(Long brandId) {
        return BrandInfo.from(brandService.getDetail(brandId));
    }
}
