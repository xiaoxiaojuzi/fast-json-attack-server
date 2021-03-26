package org.example.poorApi.contract;

public class ErrorResponse {
    private final String errorCode;
    public static final ErrorResponse UNKNOWN = new ErrorResponse("UNKNOWN");

    public ErrorResponse(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
