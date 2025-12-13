package com.springboot.medease.GlobalException;

public class QueueException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public QueueException(String message) {
        super(message);
    }
    
    public QueueException(String message, Throwable cause) {
        super(message, cause);
    }
}