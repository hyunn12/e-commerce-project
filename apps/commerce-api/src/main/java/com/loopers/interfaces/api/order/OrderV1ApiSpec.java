package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@Tag(name = "Order V1 API", description = "주문 관련 API")
public interface OrderV1ApiSpec {

    @PostMapping
    @Operation(summary = "주문 요청", description = "주문 요청 처리")
    ApiResponse<OrderV1Dto.OrderResponse.Main> order(
            @Parameter(description = "사용자 식별 USER_ID 헤더", example = "1")
            @RequestHeader(USER_USER_ID_HEADER) Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "기본 예시",
                                    value = """
                                        {
                                          "userCouponId": 1,
                                          "point": 10000,
                                          "items": [
                                              { "productId": 1, "quantity": 2 },
                                              { "productId": 3, "quantity": 1 }
                                          ]
                                        }
                                    """
                            )
                    )
            )
            @RequestBody @Valid OrderV1Dto.OrderRequest.Create request
    );

    @GetMapping
    @Operation(summary = "유저 주문 목록 조회", description = "회원 ID, 페이징 조건으로 주문 목록 조회")
    ApiResponse<OrderV1Dto.OrderResponse.Summary> summary(
            @Parameter(description = "사용자 식별 USER_ID 헤더", example = "1")
            @RequestHeader(USER_USER_ID_HEADER) Long userId,

            @ParameterObject
            @Valid OrderV1Dto.OrderRequest.Summary request
    );

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 ID 로 주문 상세 조회")
    ApiResponse<OrderV1Dto.OrderResponse.Main> getPoint(
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable Long orderId
    );
}
