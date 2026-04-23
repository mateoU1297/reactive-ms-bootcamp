package com.pragma.ms_bootcamp.domain.exception;

public class BootcampAlreadyExistsException extends RuntimeException {
    public BootcampAlreadyExistsException(String name) {
        super("Bootcamp with name '" + name + "' already exists");
    }
}
