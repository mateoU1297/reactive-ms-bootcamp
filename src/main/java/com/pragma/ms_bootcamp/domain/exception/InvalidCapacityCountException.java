package com.pragma.ms_bootcamp.domain.exception;

public class InvalidCapacityCountException extends RuntimeException {
    public InvalidCapacityCountException(String message) {
        super(message);
    }
}
