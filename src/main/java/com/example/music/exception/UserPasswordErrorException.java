package com.example.music.exception;

public class UserPasswordErrorException extends RuntimeException {
    public UserPasswordErrorException(String message) {
        super(message);
    }
}
