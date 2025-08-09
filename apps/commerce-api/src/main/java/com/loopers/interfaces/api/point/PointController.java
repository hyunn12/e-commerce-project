package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_ATTR;
import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointFacade pointFacade;

    @GetMapping
    public ApiResponse<PointDto.PointResponse> getPoint(
            HttpServletRequest httpServletRequest
    ) {
        Long userId = (Long) httpServletRequest.getAttribute(USER_USER_ID_ATTR);
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.getDetail(userId)));
    }

    @PostMapping("/charge")
    public ApiResponse<PointDto.PointResponse> charge(
            @RequestHeader(USER_USER_ID_HEADER) Long userId,
            @RequestBody @Valid PointDto.ChargeRequest request
    ) {
        return ApiResponse.success(PointDto.PointResponse.from(pointFacade.charge(request.toCommand(userId))));
    }
}
