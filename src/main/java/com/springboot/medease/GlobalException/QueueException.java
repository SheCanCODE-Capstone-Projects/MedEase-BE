package com.springboot.medease.GlobalException;

public class QueueException extends RuntimeException {
    public QueueException(String message) {
        super(message);
    }
}