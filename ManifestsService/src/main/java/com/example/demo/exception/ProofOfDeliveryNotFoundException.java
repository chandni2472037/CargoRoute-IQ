package com.example.demo.exception;

public class ProofOfDeliveryNotFoundException extends RuntimeException {
    public ProofOfDeliveryNotFoundException(String message) {
        super(message);
    }
}