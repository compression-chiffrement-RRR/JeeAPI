package com.cyphernet.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
@Getter
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access Denied");
    }
}
