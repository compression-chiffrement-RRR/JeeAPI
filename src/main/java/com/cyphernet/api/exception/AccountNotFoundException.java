package com.cyphernet.api.exception;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(String fieldName, Object fieldValue) {
        super("Account", fieldName, fieldValue);
    }
}
