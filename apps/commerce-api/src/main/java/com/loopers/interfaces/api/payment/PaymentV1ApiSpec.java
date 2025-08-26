package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@Tag(name = "Payment V1 API", description = "결제 관련 API")
public interface PaymentV1ApiSpec {

    @Operation(summary = "결제 요청", description = "결제 요청 처리")
    ApiResponse<PaymentV1Dto.PaymentResponse.Main> payment(
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
                                          "orderId": 1,
                                          "cardType": "SAMSUNG",
                                          "cardNo": "1234-5678-9814-1451",
                                          "paymentAmount": 5000
                                        }
                                    """
                            )
                    )
            )
            @RequestBody PaymentV1Dto.PaymentRequest.Create request
    );

    @Operation(summary = "결제 결과 callback", description = "결제 결과 처리")
    ApiResponse<?> callback(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "기본 예시",
                                    value = """
                                        {
                                          "orderNo": "5d30ba6c-4742-429e-ad43-05b00bcb0a7b",
                                          "transactionKey": "0250821:TR:11d3e4",
                                          "status": "SUCCESS",
                                          "reason": "정상 승인되었습니다."
                                        }
                                    """
                            )
                    )
            )
            @RequestBody PaymentV1Dto.PaymentRequest.Callback request
    );
}
