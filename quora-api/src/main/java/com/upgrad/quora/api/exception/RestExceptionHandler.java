package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> UserNotFoundException(UserNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
}
