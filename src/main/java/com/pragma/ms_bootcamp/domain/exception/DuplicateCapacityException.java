package com.pragma.ms_bootcamp.domain.exception;

public class DuplicateCapacityException extends RuntimeException {
    public DuplicateCapacityException() {
        super("Bootcamp cannot have duplicate capacities");
    }
}
