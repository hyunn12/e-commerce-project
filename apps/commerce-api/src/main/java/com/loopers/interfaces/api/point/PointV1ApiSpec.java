package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@Tag(name = "Point V1 API", description = "포인트 관련 API")
public interface PointV1ApiSpec {

    @GetMapping
    @Operation(summary = "포인트 조회", description = "회원 ID 로 보유 포인트 조회")
    ApiResponse<PointV1Dto.PointResponse> getPoint(
            @Parameter(description = "사용자 식별 USER_ID 헤더", example = "1")
            @RequestHeader(USER_USER_ID_HEADER) Long userId
    );

    @PostMapping("/charge")
    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 주어진 금액만큼 충전")
    ApiResponse<PointV1Dto.PointResponse> charge(
            @Parameter(description = "사용자 식별 USER_ID 헤더", example = "1")
            @RequestHeader(USER_USER_ID_HEADER) Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"amount\": 10000 }")
                    )
            )
            @RequestBody @Valid PointV1Dto.ChargeRequest request
    );
}
