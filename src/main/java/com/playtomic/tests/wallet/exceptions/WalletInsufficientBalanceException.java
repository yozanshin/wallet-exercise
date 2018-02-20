package com.playtomic.tests.wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WalletInsufficientBalanceException extends Exception {

    public WalletInsufficientBalanceException(String message) {
        super(message);
    }
}
