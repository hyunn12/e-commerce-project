package com.loopers.application.payment;

import java.util.Map;

public interface PaymentAlertSender {

    void sendFail(Map<String, Object> params, Exception e);
}
