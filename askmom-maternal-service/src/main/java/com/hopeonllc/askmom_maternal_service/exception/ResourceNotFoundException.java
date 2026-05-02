package com.hopeonllc.askmom_maternal_service.exception;

import com.hopeonllc.askmom_maternal_service.enums.ErrorCode;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
