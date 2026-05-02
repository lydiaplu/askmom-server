package com.hopeonllc.askmom_maternal_service.exception;

import com.hopeonllc.askmom_maternal_service.enums.ErrorCode;

/**
 * 业务异常的父类。
 * 统一要求每个业务异常都携带一个 ErrorCode，便于全局异常处理和前端识别错误类型。
 */
public abstract class BusinessException extends RuntimeException {
    /**
     * 给系统使用的错误码（机器可识别），用于区分具体错误类型。
     */
    private final ErrorCode errorCode;

    /**
     * @param errorCode 业务错误码（给系统判断）
     * @param message   错误描述（给人阅读）
     */
    protected BusinessException(ErrorCode errorCode, String message) {
        // 把可读的错误信息交给 RuntimeException 保存
        super(message);
        // 保存业务错误码，异常对象创建后不再变化
        this.errorCode = errorCode;
    }

    /**
     * 供外部（如全局异常处理器）读取当前异常对应的错误码。
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
