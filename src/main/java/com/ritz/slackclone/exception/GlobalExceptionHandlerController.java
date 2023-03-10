package com.ritz.slackclone.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ritz.slackclone.exception.base.ApiError;
import com.ritz.slackclone.exception.base.ApiSubError;

@EnableWebMvc
@ControllerAdvice
public class GlobalExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ApiSubError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ApiSubError(error.getField(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(new ApiSubError(error.getObjectName(), error.getDefaultMessage()));
        }

        ApiError apiError = new ApiError(status, "Invalid field error", errors);

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    @ExceptionHandler({ ResourceAlreadyExistsException.class, BadCredentialsException.class })
    public final ResponseEntity<Object> handleResourceAlreadyExistsException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex,
                new ApiError(status, ex.getMessage(), Arrays.asList(new ApiSubError("username", ex.getMessage()))),
                headers,
                status, request);

    }

}
