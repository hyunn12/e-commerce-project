package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<ProductV1Dto.ProductResponse.Summary> getList(
            @Valid ProductV1Dto.ProductRequest.Summary request
    ) {
        return ApiResponse.success(ProductV1Dto.ProductResponse.Summary.from(productFacade.getList(request.toCommand())));
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse.Detail> getDetail(
            @PathVariable Long productId
    ) {
        return ApiResponse.success(ProductV1Dto.ProductResponse.Detail.from(productFacade.getDetail(productId)));
    }
}
