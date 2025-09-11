package com.loopers.interfaces.api.ranking;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Ranking V1 API", description = "상품 랭킹 조회 API")
public interface RankingV1ApiSpec {

    @GetMapping
    @Operation(summary = "상품 랭킹 조회", description = "날짜, 페이징 조건으로 상품 랭킹 목록 조회")
    ApiResponse<RankingV1Dto.RankingResponse.Summary> getList(
            @ParameterObject
            @Valid RankingV1Dto.RankingRequest.Summary request
    );
}
