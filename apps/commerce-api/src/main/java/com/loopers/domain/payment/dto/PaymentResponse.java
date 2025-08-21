package com.loopers.domain.payment.dto;

import com.loopers.infrastructure.client.pg.PgClientDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse {
    private String transactionKey;
    private String orderNo;
    private String status;
    private String reason;

    public static PaymentResponse from(PgClientDto.PgResponse response) {
        return new PaymentResponse(
                response.transactionKey(),
                response.orderId(),
                response.status(),
                response.reason()
        );
    }

    public static PaymentResponse fail(String reason) {
        return new PaymentResponse(null, null, "FAIL", reason);
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionKey='" + transactionKey + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
