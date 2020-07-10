package com.cyphernet.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class CreationException extends RuntimeException {
    public CreationException() {
        super("Creation exception");
    }
}
