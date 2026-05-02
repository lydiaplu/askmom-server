package com.hopeonllc.askmom_baby_service.exception;

import com.hopeonllc.askmom_baby_service.enums.ErrorCode;

public class BabyAlreadyExistsException extends BusinessException {
    public BabyAlreadyExistsException(String message) {
        super(ErrorCode.BABY_ALREADY_EXISTS, message);
    }
}
