package com.hopeonllc.askmom_maternal_service.security;

import com.hopeonllc.askmom_maternal_service.security.jwt.AuthTokenFilter;
import com.hopeonllc.askmom_maternal_service.security.jwt.JwtAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 主配置类。
 * <p>
 * 该配置采用 JWT + 无状态会话（STATELESS）模式：
 * 1. 请求通过过滤器链进行认证与鉴权；
 * 2. 登录状态不保存在服务端 Session 中；
 * 3. 受保护接口依赖每次请求携带的 token 完成身份校验。
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * 未认证访问处理入口。
     * 当请求需要认证但认证失败或缺失认证信息时，由该组件统一返回响应（例如 401）。
     */
    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    /**
     * 注册 JWT 认证过滤器。
     * 过滤器通常负责：
     * 1. 从请求中提取 token；
     * 2. 校验 token 有效性；
     * 3. 将认证结果写入 SecurityContext。
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * 密码编码器配置。
     * BCrypt 是常用的安全哈希算法，适用于密码存储与校验。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * maternal-service 不保存用户账号密码，也不负责登录。
     * 显式提供 UserDetailsService，避免 Spring Boot 自动创建默认内存用户。
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Local user authentication is not supported in maternal-service");
        };
    }

    /**
     * HTTP 安全过滤链配置（核心）。
     * <p>
     * 当前规则说明：
     * 1. 关闭 CSRF（适用于典型 JWT 无状态接口场景）；
     * 2. 指定未认证处理入口；
     * 3. 会话策略设为无状态；
     * 4. maternal-service 不提供登录接口；
     * 5. 所有请求均要求已认证。
     * <p>
     * 过滤器顺序：
     * JWT 过滤器放在 UsernamePasswordAuthenticationFilter 之前，
     * 使请求优先尝试 token 认证。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
