package org.example.poorApi.web;

import org.example.poorApi.contract.ErrorResponse;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiException extends NestedRuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public ApiException(HttpStatus status, String errorCode) {
        this(status, errorCode, null);
    }

    public ApiException(HttpStatus status, String errorCode, Throwable cause) {
        super(null, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity.status(status.value()).body(new ErrorResponse(errorCode));
    }
}
