package com.soracel.library_system.payments;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
