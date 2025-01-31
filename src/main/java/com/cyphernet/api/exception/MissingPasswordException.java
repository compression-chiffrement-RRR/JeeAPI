package com.cyphernet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password missing")
public class MissingPasswordException extends RuntimeException {
    private static final long serialVersionUID = 200L;
}
