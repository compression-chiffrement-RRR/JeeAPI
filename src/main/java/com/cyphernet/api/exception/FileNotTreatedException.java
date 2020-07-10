package com.cyphernet.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED, reason = "File is not treated for the moment")
public class FileNotTreatedException extends Exception {
    private static final long serialVersionUID = 400L;
}
