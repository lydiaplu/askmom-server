package com.hopeonllc.askmom_user_service.exception;

import com.hopeonllc.askmom_user_service.enums.ErrorCode;

public class UserProfileAlreadyExistsException extends BusinessException {
    public UserProfileAlreadyExistsException(String message) {
        super(ErrorCode.USER_PROFILE_ALREADY_EXISTS, message);
    }
}
