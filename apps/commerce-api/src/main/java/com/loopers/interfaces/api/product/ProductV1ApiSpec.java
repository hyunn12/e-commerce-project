package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Product V1 API", description = "상품 조회 API")
public interface ProductV1ApiSpec {

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "브랜드 ID, 정렬, 페이징 조건으로 상품 목록 조회")
    ApiResponse<ProductV1Dto.ProductResponse.Summary> getList(
            @ParameterObject
            @Valid ProductV1Dto.ProductRequest.Summary request
    );

    @GetMapping("/{productId}")
    @Operation(summary = "상품 정보 조회", description = "상품 ID 로 상품 상세 정보 조회")
    ApiResponse<ProductV1Dto.ProductResponse.Detail> getDetail(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable Long productId
    );
}
