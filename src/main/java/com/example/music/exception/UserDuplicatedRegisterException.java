package com.example.music.exception;

public class UserDuplicatedRegisterException extends RuntimeException {
    public UserDuplicatedRegisterException(String message) {
        super(message);
    }
}
