package com.example.demo.Exception;

public class KPINotFoundException extends RuntimeException {
    public KPINotFoundException(String message) {
        super(message);
    }
}