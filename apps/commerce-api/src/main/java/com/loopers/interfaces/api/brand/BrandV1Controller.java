package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec{

    private final BrandFacade brandFacade;

    @GetMapping("/{brandId}")
    public ApiResponse<BrandV1Dto.BrandResponse> getDetail(
            @PathVariable Long brandId
    ) {
        return ApiResponse.success(BrandV1Dto.BrandResponse.from(brandFacade.getDetail(brandId)));
    }

    @Override
    @PostMapping
    public ApiResponse<BrandV1Dto.BrandResponse> modify(
            @RequestBody BrandV1Dto.BrandRequest request
    ) {
        return ApiResponse.success(BrandV1Dto.BrandResponse.from(brandFacade.modify(request.toCommand())));
    }
}
