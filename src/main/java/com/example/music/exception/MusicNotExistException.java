package com.example.music.exception;

public class MusicNotExistException extends RuntimeException{
    public MusicNotExistException(String message) {
        super(message);
    }
}
