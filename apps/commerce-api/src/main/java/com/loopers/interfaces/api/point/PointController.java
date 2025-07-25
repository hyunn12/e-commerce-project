package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointFacade pointFacade;

    @GetMapping
    public ApiResponse<PointDto.PointResponse> getPoint(
            HttpServletRequest httpServletRequest
    ) {
        String userId = (String) httpServletRequest.getAttribute("userId");
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.getPointByUserId(userId)));
    }

    @PostMapping("/charge")
    public ApiResponse<PointDto.PointResponse> charge(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody @Valid PointDto.ChargeRequest request
    ) {
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.charge(request.toCommand(userId))));
    }
}
