package com.cyphernet.api.exception;

public class UserFileNotFoundException extends ResourceNotFoundException {
    public UserFileNotFoundException(String fieldName, Object fieldValue) {
        super("UserFile", fieldName, fieldValue);
    }
}
