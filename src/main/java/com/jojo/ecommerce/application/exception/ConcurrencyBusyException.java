package com.jojo.ecommerce.application.exception;

public class ConcurrencyBusyException extends RuntimeException {
    public ConcurrencyBusyException(String msg) {
        super(msg);
    }
}