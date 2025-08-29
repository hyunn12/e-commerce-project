package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Payment V1 API", description = "결제 관련 API")
public interface PaymentV1ApiSpec {

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
