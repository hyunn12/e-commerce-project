package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointFacade pointFacade;

    @PostMapping("/charge")
    public ApiResponse<PointDto.PointResponse> charge(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PointDto.ChargeRequest chargeRequest
    ) {
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.charge(chargeRequest.toInfo(userId))));
    }

    @GetMapping
    public ApiResponse<PointDto.PointResponse> getPoint(
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.getPointByUserId(userId)));
    }

}
