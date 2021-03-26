package org.example.poorApi.web;

import org.example.poorApi.contract.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ApiException error) {
        logger.warn(error.toString());
        return error.toResponseEntity();
    }
}
