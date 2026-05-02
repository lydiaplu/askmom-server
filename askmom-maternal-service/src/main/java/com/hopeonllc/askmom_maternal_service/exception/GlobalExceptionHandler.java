package com.hopeonllc.askmom_maternal_service.exception;

import com.hopeonllc.askmom_maternal_service.enums.ErrorCode;
import com.hopeonllc.askmom_maternal_service.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 * 统一处理所有已知异常，只要程序报错，它都会接管，并返回标准的错误响应格式。
 *
 * 处理的是“请求处理流程中”最终冒泡到 Controller 层这条链路上的异常。
 * 它会根据不同的异常类型，返回不同的 HTTP 状态码和错误信息。
 */

// @RestControllerAdvice 表示“全项目的 Controller 异常都交给我处理”
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 统一处理“请求前端传递来的参数校验失败”异常，返回 400 响应。
     *
     * MethodArgumentNotValidException 主要处理 body（@RequestBody + @Valid）校验错误
     * 常见触发场景：Controller 使用 @Valid @RequestBody 时，
     * Request DTO（如 MaternalProfileRequest）上的约束注解校验失败，
     * 例如 @Size、@NotNull、@Min。
     *
     * 注意：它不覆盖所有参数校验失败场景。
     * - @RequestParam / @PathVariable + @Validated，常见是 ConstraintViolationException
     * - @ModelAttribute 绑定失败，常见是 BindException
     *
     * @param ex：校验失败的异常详情（字段错误、提示信息等）
     * @param request：当前 HTTP 请求（可用于获取请求路径等上下文）
     * @return 返回：统一的 400 错误响应体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        // 把每个字段错误变成文本，然后用 ; 拼起来。
        // 例如可能得到：userId: must not be null; babyCount: must be greater than or equal to 1
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Request validation failed: path={}, message={}", request.getRequestURI(), message);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ErrorCode.DATA_VALIDATION_ERROR.name(), // 得到枚举常量名字符串："DATA_VALIDATION_ERROR"
                message,
                HttpStatus.BAD_REQUEST.value(), // 得到 HTTP 状态码数字：400
                LocalDateTime.now(),
                request.getRequestURI() // 返回本次请求的路径部分（URI path)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException：主要处理 URL/查询参数/方法参数
    // （如 @PathVariable, @RequestParam）校验错误（通常配 @Validated）
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Constraint violation: path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ErrorCode.DATA_VALIDATION_ERROR.name(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = isConflictConstraint(ex) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        log.warn("Data integrity violation: path={}, status={}, message={}",
                request.getRequestURI(), status.value(), ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ErrorCode.DATA_VALIDATION_ERROR.name(),
                buildDataIntegrityMessage(ex, status),
                status.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        log.warn("Authentication failed: path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "AUTHENTICATION_FAILED",
                "Invalid or missing token",
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 如果代码里抛了 BusinessException，就走这个方法
    // 本质是 Spring MVC 执行 Controller 时，如果发生异常，会查找匹配的 @ExceptionHandler 方法来处理。
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        // 先根据错误的实例找到对应的错误码，然后映射到 HTTP 状态码
        HttpStatus status = mapBusinessStatus(ex);
        log.warn("Business exception: path={}, errorCode={}, status={}, message={}",
                request.getRequestURI(), ex.getErrorCode().name(), status.value(), ex.getMessage());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ex.getErrorCode().name(), // 得到枚举常量名的字符串
                ex.getMessage(),
                status.value(), // 返回 HTTP 状态码的整数值
                LocalDateTime.now(),
                request.getRequestURI() // 返回本次请求的路径部分（URI path)
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiErrorResponse> handleSystemException(
            SystemException ex,
            HttpServletRequest request
    ) {
        log.error("System exception: path={}, errorCode={}, message={}",
                request.getRequestURI(), ex.getErrorCode().name(), ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ex.getErrorCode().name(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 表示 HTTP 状态“服务器内部错误” 500
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception: path={}, message={}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "UNEXPECTED_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus mapBusinessStatus(BusinessException ex) {
        if (ex instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof DataValidationException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof MaternalProfileAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private boolean isConflictConstraint(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        if (message == null) {
            return false;
        }

        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("duplicate")
                || lowerMessage.contains("unique")
                || lowerMessage.contains("uk_");
    }

    private String buildDataIntegrityMessage(DataIntegrityViolationException ex, HttpStatus status) {
        if (status == HttpStatus.CONFLICT) {
            return "Data conflict: unique constraint violated";
        }
        return "Data validation failed: database constraint violated";
    }
}
