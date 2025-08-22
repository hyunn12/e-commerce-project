package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(Long userId, Long orderId, int paymentAmount, PaymentMethod method) {
        Payment payment = Payment.createBuilder()
                .userId(userId)
                .orderId(orderId)
                .paymentAmount(paymentAmount)
                .method(method)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment getDetail(Long paymentId) {
        Payment payment = paymentRepository.getDetail(paymentId);
        if (payment == null) {
            throw new IllegalStateException("결제 정보를 찾을 수 없습니다.");
        }
        return payment;
    }

    public Payment getDetailByKey(String transactionKey) {
        Payment payment = paymentRepository.getDetailByKey(transactionKey);
        if (payment == null) {
            throw new IllegalStateException("결제 정보를 찾을 수 없습니다.");
        }
        return payment;
    }

    public void checkPendingPayment(Payment payment) {
        Payment checkPayment = paymentRepository.getDetail(payment.getId());
        if (checkPayment == null) {
            throw new IllegalStateException("결제 정보를 찾을 수 없습니다.");
        }
        if (!checkPayment.getTransactionKey().equals(payment.getTransactionKey())) {
            throw new IllegalStateException("Transaction Key가 일치하지 않습니다.");
        }
        if (payment.getStatus() == PaymentStatus.PENDING) {
            throw new IllegalStateException("결제 상태가 대기중이 아닙니다. status: " + payment.getStatus());
        }
    }

    public List<Payment> getListPendingPayments() {
        ZonedDateTime before = ZonedDateTime.now().minusMinutes(5);
        return paymentRepository.getListPendingPayments(PaymentMethod.CARD, PaymentStatus.PENDING, before);
    }
}
