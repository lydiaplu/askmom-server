package com.hopeonllc.askmom_user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 请求日志过滤器：
 * 1) 为每个请求生成/透传 traceId、requestId。
 * 2) 将关键字段放入 MDC，便于日志中串联请求链路。
 * 3) 在响应头回写 X-Request-Id / X-Trace-Id 便于排障。
 * 4) 记录请求开始与结束日志（状态码、耗时）。
 *
 * 未来可能风险：
 * - 直接信任外部传入的 X-Trace-Id/X-Request-Id，可能被伪造或污染日志。
 * - MDC.clear() 会清空线程上所有 MDC，可能误删其他组件放入的上下文字段。
 * - 异步线程/线程池场景下 MDC 可能无法自动透传，导致链路日志断裂。
 * - 高并发下每请求两条 info 日志会增加 I/O 与存储成本。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    // MDC key：日志 pattern 中可通过 %X{traceId} / %X{requestId} 输出
    private static final String TRACE_ID_KEY = "traceId";
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String PATH_KEY = "path";
    private static final String METHOD_KEY = "method";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 记录请求开始时间，用于统计耗时
        long startTime = System.currentTimeMillis();

        // 优先透传上游 ID；无值时本地生成
        String traceId = resolveTraceId(request);
        String requestId = resolveRequestId(request);
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 将请求上下文写入 MDC，便于日志聚合检索
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(PATH_KEY, path);
        MDC.put(METHOD_KEY, method);

        // 在响应头透出链路 ID，便于客户端/网关问题排查
        response.setHeader("X-Request-Id", requestId);
        response.setHeader("X-Trace-Id", traceId);

        log.info("Request started: method={}, path={}", method, path);
        try {
            // 放行请求到后续过滤器与控制器
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("Request completed: method={}, path={}, status={}, durationMs={}",
                    method, path, response.getStatus(), durationMs);
            // 清理 MDC，避免线程复用导致上下文串值
            MDC.clear();
        }
    }

    // 解析 traceId：优先使用请求头；缺失时生成无连字符 UUID
    private String resolveTraceId(HttpServletRequest request) {
        String incomingTraceId = request.getHeader("X-Trace-Id");
        if (incomingTraceId != null && !incomingTraceId.isBlank()) {
            return incomingTraceId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 解析 requestId：优先使用请求头；缺失时生成无连字符 UUID
    private String resolveRequestId(HttpServletRequest request) {
        String incomingRequestId = request.getHeader("X-Request-Id");
        if (incomingRequestId != null && !incomingRequestId.isBlank()) {
            return incomingRequestId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
