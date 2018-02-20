package com.playtomic.tests.wallet.service.payment;

import com.playtomic.tests.wallet.service.payment.exceptions.PaymentServiceException;

import java.math.BigDecimal;

public interface PaymentService {
    void charge(BigDecimal amount) throws PaymentServiceException;
}
