package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @GetMapping
    public ApiResponse<PointV1Dto.PointResponse> getPoint(
            @RequestHeader(USER_USER_ID_HEADER) Long userId
    ) {
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointFacade.getDetail(userId)));
    }

    @PostMapping("/charge")
    public ApiResponse<PointV1Dto.PointResponse> charge(
            @RequestHeader(USER_USER_ID_HEADER) Long userId,
            @RequestBody @Valid PointV1Dto.ChargeRequest request
    ) {
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointFacade.charge(request.toCommand(userId))));
    }
}
