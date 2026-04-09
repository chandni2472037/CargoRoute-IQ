package com.example.demo.exception;

public class HandoverNotFoundException extends RuntimeException {
    public HandoverNotFoundException(String message) {
        super(message);
    }
}