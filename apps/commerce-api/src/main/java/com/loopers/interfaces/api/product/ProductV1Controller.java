package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller {

    private final ProductFacade productFacade;

    // 상품 목록 조회
    @GetMapping
    public ApiResponse<ProductV1Dto.ProductResponse.Summary> getList(
//         @RequestParam ProductV1Dto.ProductRequest.Summary command
    ) {
        return ApiResponse.success(
                new ProductV1Dto.ProductResponse.Summary(List.of(), 1,20)
        );
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse.Detail> getDetail(
            @PathVariable String productId
    ) {
        return ApiResponse.success(
                new ProductV1Dto.ProductResponse.Detail(1L, "브랜드명", 10000, 10, "브랜드명", "브랜드설명", 100)
        );
    }
}
