package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Brand V1 API", description = "브랜드 관련 API")
public interface BrandV1ApiSpec {

    @GetMapping("/{brandId}")
    @Operation(summary = "브랜드 상세 조회", description = "브랜드 ID 로 브랜드 상세 정보 조회")
    ApiResponse<BrandV1Dto.BrandResponse> getDetail(
            @Parameter(description = "브랜드 ID", example = "1")
            @PathVariable Long brandId
    );

    @Operation(summary = "브랜드 정보 수정", description = "브랜드 ID 로 해당 브랜드 정보 수정")
    ApiResponse<BrandV1Dto.BrandResponse> modify(
            BrandV1Dto.BrandRequest request
    );
}
