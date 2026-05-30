package com.example.music.exception;

public class UserFailActivatedException extends RuntimeException {
    public UserFailActivatedException(String message) {
        super(message);
    }
}
