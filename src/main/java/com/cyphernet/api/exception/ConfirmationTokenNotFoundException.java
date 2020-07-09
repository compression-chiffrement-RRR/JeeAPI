package com.cyphernet.api.exception;

public class ConfirmationTokenNotFoundException extends ResourceNotFoundException {
    public ConfirmationTokenNotFoundException(String fieldName, Object fieldValue) {
        super("Confirmation token", fieldName, fieldValue);
    }
}
