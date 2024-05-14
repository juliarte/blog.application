package com.system.blog.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class FailedToUpdateException extends RuntimeException {

    public FailedToUpdateException(String message) {
        super(message);
    }
    
}
