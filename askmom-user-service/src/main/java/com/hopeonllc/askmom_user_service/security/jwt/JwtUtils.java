package com.hopeonllc.askmom_user_service.security.jwt;

import com.hopeonllc.askmom_user_service.security.user.AskmomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

// JWT 工具类，用于生成和验证 JWT
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret; // 从配置文件中读取 JWT 密钥

    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationMs; // 从配置文件中读取 JWT 过期时间

    // 为用户生成 JWT
    public String generateJwtTokenForUser(Authentication authentication) {
        // 从登录成功的认证结果里，取出当前登录用户对象，并转成你自定义类型
        AskmomUserDetails userPrincipal = (AskmomUserDetails) authentication.getPrincipal();

        // 获取用户角色列表
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 构建并返回 JWT
        return Jwts
                .builder() // 创建一个 JwtBuilder 实例，用于构建 JWT
                .setSubject(userPrincipal.getUsername()) // 设置 JWT 的主题，通常用于存储用户标识
                .claim("roles", roles) // 添加自定义声明（例如用户角色）
                .setIssuedAt(new Date()) // 设置 JWT 签发时间
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // 设置 JWT 过期时间
                .signWith(key(), SignatureAlgorithm.HS256) // 使用指定的密钥和算法对 JWT 进行签名
                .compact(); // 构建并压缩 JWT 为一个字符串
    }

    // 把配置里的字符串转换成签名算法需要的 Key 对象
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 从 JWT 中获取用户名
    public String getUserNameFromToken(String token) {
        return Jwts
                .parserBuilder() // 返回一个 JWT 解析器构建器
                .setSigningKey(key()) // 设置用于签名验证的密钥
                .build() // 构建 JWT 解析器实例
                .parseClaimsJws(token) // 解析并验证 JWS，返回 Jws<Claims> 对象，其中包含 JWT 的声明
                .getBody() // 获取 JWT 的主体部分（即声明）
                .getSubject(); // 从声明中获取主题（通常是用户名）
    }

    // 验证 JWT
    public boolean validateToken(String token) {
        try {
            // 解析 JWT
            Jwts
                    .parserBuilder() // 返回一个 JWT 解析器构建器
                    .setSigningKey(key()) // 设置用于签名验证的密钥
                    .build() // 构建 JWT 解析器实例
                    .parse(token); // 解析并验证 JWT，如果解析失败会抛出相应的异常
            return true;
        } catch (MalformedJwtException e) {
            // 无效 JWT
            logger.error("Invalid jwt token : {} ", e.getMessage());
        } catch (ExpiredJwtException e) {
            // JWT 过期
            logger.error("Expired token : {} ", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 不支持的 JWT
            logger.error("This token is not supported : {} ", e.getMessage());
        } catch (IllegalArgumentException e) {
            // 空的 JWT 声明
            logger.error("No  claims found : {} ", e.getMessage());
        }
        return false; // 如果有任何异常，返回 false 表示无效 JWT
    }
}
