package com.hopeonllc.askmom_user_service.security.user;

import com.hopeonllc.askmom_user_service.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 把数据库里的 User，包装成 Spring Security 认证体系内部统一使用的用户对象。
 */
public class AskmomUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;

    public AskmomUserDetails(Long id, String email, String password, boolean active,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    /**
     * 把数据库里的 User 实体 转成 AskmomUserDetails
     * @param user 数据库中的 User 实体
     * @return 包装后的 AskmomUserDetails 对象
     */
    public static AskmomUserDetails buildUserDetails(User user) {
        // 当前写法固定给权限：new SimpleGrantedAuthority("ROLE_USER")
        // List<GrantedAuthority> authorities 是“权限列表变量”
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new AskmomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
