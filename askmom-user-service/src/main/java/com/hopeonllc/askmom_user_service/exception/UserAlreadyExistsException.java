package com.hopeonllc.askmom_user_service.exception;

import com.hopeonllc.askmom_user_service.enums.ErrorCode;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, message);
    }
}
