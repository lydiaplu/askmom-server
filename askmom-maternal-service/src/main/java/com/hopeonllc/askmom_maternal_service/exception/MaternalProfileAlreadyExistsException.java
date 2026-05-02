package com.hopeonllc.askmom_maternal_service.exception;

import com.hopeonllc.askmom_maternal_service.enums.ErrorCode;

public class MaternalProfileAlreadyExistsException extends BusinessException {
    public MaternalProfileAlreadyExistsException(String message) {
        super(ErrorCode.MATERNAL_PROFILE_ALREADY_EXISTS, message);
    }
}
