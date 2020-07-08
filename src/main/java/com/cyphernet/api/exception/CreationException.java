package com.cyphernet.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@Getter
public class CreationException extends RuntimeException {
    public CreationException() {
        super("Creation exception");
    }
}
