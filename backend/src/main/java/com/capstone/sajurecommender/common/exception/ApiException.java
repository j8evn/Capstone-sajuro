package com.capstone.sajurecommender.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom API exception for structured error responses.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = status.name();
    }

    public ApiException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = status.name();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // ==================== Factory Methods ====================

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public static ApiException internalError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", message);
    }

    public static ApiException invalidInput(String field, String detail) {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                String.format("입력값 오류 [%s]: %s", field, detail));
    }
}
