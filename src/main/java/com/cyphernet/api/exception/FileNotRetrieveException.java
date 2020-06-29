package com.cyphernet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "File retrieve failed")
public class FileNotRetrieveException extends Exception {
    private static final long serialVersionUID = 200L;
}
