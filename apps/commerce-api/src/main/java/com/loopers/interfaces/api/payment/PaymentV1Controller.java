package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

    private final PaymentFacade paymentFacade;

    @PostMapping
    @Override
    public ApiResponse<PaymentV1Dto.PaymentResponse.Main> payment(
            Long userId,
            PaymentV1Dto.PaymentRequest.Create request
    ) {
        return ApiResponse.success(PaymentV1Dto.PaymentResponse.Main.from(paymentFacade.payment(request.toCommand(userId))));
    }

    @PostMapping("/callback")
    @Override
    public ApiResponse<String> callback(PaymentV1Dto.PaymentRequest.Callback request) {
        paymentFacade.paymentCallback(request.toCommand());
        return ApiResponse.success("SUCCESS");
    }
}
