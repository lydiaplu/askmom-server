package com.hopeonllc.askmom_baby_service.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // get jwt
            String jwt = parseJwt(request);

            // 如果 JWT 存在并且有效，则进行用户身份验证
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                // get user email from jwt token
                String email = jwtUtils.getUserNameFromToken(jwt);

                // 创建认证对象
                var authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置安全上下文中的认证信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 记录身份验证过程中出现的错误
            logger.error("Cannot set user authentication : {} ", e.getMessage());
        }

        // 确保在尝试身份验证后继续处理请求，即使没有设置身份验证
        filterChain.doFilter(request, response);
    }

    // 从请求头中解析 JWT
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        // 检查请求头是否包含 JWT，并且以 "Bearer " 开头
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // 返回 JWT 字符串
        }
        return null; // 否则返回 null
    }
}
