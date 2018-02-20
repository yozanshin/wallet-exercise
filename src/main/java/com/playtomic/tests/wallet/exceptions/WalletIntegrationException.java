package com.playtomic.tests.wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WalletIntegrationException extends Exception {

    public WalletIntegrationException(String message) {
        super(message);
    }
}
