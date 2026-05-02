package com.hopeonllc.askmom_baby_service.exception;

import com.hopeonllc.askmom_baby_service.enums.ErrorCode;

public class SystemException extends RuntimeException {
    private final ErrorCode errorCode;

    public SystemException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
