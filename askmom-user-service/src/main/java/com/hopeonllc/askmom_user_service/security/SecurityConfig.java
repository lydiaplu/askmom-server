package com.hopeonllc.askmom_user_service.security;

import com.hopeonllc.askmom_user_service.security.jwt.AuthTokenFilter;
import com.hopeonllc.askmom_user_service.security.jwt.JwtAuthEntryPoint;
import com.hopeonllc.askmom_user_service.security.user.AskmomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
     * 自定义用户信息加载服务。
     * DaoAuthenticationProvider 在执行用户名/密码认证时会调用该服务查询用户。
     */
    @Autowired
    private AskmomUserDetailsService userDetailsService;

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
     * 配置基于数据库（DAO）的认证提供者。
     * <p>
     * 关键依赖：
     * 1. UserDetailsService：加载用户账号信息；
     * 2. PasswordEncoder：执行密码哈希匹配。
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 暴露 AuthenticationManager Bean。
     * 业务层登录逻辑可注入该组件并调用 authenticate(...) 触发认证流程。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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
     * HTTP 安全过滤链配置（核心）。
     * <p>
     * 当前规则说明：
     * 1. 关闭 CSRF（适用于典型 JWT 无状态接口场景）；
     * 2. 指定未认证处理入口；
     * 3. 会话策略设为无状态；
     * 4. 放行登录接口 /auth/login；
     * 5. 其余请求均要求已认证。
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
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
