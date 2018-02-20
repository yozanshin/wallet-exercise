package com.playtomic.tests.wallet.service.payment.impl;


import com.playtomic.tests.wallet.service.payment.exceptions.PaymentServiceException;
import org.junit.Test;

import java.math.BigDecimal;

public class ThirdPartyPaymentServiceTest {

    ThirdPartyPaymentService thirdPartyPaymentService = new ThirdPartyPaymentService();

    @Test(expected = PaymentServiceException.class)
    public void test_exception() throws PaymentServiceException {
        thirdPartyPaymentService.charge(new BigDecimal(5));
    }

    @Test
    public void test_ok() throws PaymentServiceException {
        thirdPartyPaymentService.charge(new BigDecimal(15));
    }
}
