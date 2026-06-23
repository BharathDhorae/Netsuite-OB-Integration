package com.promanatia.CamelDemo.exception;

public class DocumentIdMissingException extends RuntimeException {
    public DocumentIdMissingException(String message) {
        super(message);
    }
}