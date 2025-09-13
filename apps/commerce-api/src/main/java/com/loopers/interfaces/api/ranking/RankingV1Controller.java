package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingV1Controller implements RankingV1ApiSpec {

    private final RankingFacade rankingFacade;

    @GetMapping
    @Override
    public ApiResponse<RankingV1Dto.RankingResponse.Summary> getList(
            @Valid RankingV1Dto.RankingRequest.Summary request
    ) {
        return ApiResponse.success(RankingV1Dto.RankingResponse.Summary.from(rankingFacade.getList(request.toCommand())));
    }
}
