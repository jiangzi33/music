package com.example.music.exception;

public class EmailFailActivatedException extends RuntimeException {
    public EmailFailActivatedException(String message) {
        super(message);
    }
}
