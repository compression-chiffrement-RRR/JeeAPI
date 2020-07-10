package com.cyphernet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Missing param")
public class MissingParamException extends RuntimeException {
    private static final long serialVersionUID = 500L;
}
