package com.example.demo.exception;

public class ManifestNotFoundException extends RuntimeException {
    public ManifestNotFoundException(String message) {
        super(message);
    }
}
