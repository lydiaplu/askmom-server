package com.hopeonllc.askmom_user_service.exception;

import com.hopeonllc.askmom_user_service.enums.ErrorCode;

// 数据校验异常：用于“参数合法性不通过”的业务场景（例如邮箱为空、格式错误等）。
// 继承 BusinessException 的目的：
// 1) 让它进入统一的业务异常处理流程（GlobalExceptionHandler 的 BusinessException 分支）。
// 2) 保留 errorCode + message 结构，前端可以稳定解析错误类型。
public class DataValidationException extends BusinessException {

    // 构造器只接收 message，把 ErrorCode 固定为 DATA_VALIDATION_ERROR。
    // 这样调用方不需要每次手动传错误码，避免传错，保持同类异常的一致性。
    public DataValidationException(String message) {
        super(ErrorCode.DATA_VALIDATION_ERROR, message);
    }
}
