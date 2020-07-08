package com.cyphernet.api.exception;

public class PendingInvitationNotFoundException extends ResourceNotFoundException {
    public PendingInvitationNotFoundException(String fieldName, Object fieldValue) {
        super("Pending invitation", fieldName, fieldValue);
    }
}
