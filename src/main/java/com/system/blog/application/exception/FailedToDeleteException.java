package com.system.blog.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class FailedToDeleteException extends RuntimeException {

    public FailedToDeleteException(String message) {
        super(message);
    }
    
}
