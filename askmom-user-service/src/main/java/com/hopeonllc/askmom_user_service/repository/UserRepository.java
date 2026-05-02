package com.hopeonllc.askmom_user_service.repository;

import com.hopeonllc.askmom_user_service.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository 层建议使用接口（interface），因为 Spring Data JPA 会在运行时自动生成实现类。
// 只需要定义“要查什么”，不需要自己手写“怎么查”，可以减少大量样板代码。
// 这也符合依赖倒置原则：Service 依赖抽象接口，而不是依赖具体实现类。
public interface UserRepository extends JpaRepository<User, Long> {
    // 为什么是 extends JpaRepository<User, Long>：
    // 1) JpaRepository 提供通用 CRUD 能力（save/findById/findAll/deleteById 等）。
    // 2) <User, Long> 中：
    //    - User 表示当前 Repository 管理的实体类型是 User。
    //    - Long 表示该实体主键类型是 Long（对应 User 的 id 字段）。
    // 3) 继承后可直接使用分页、排序等能力，无需重复造轮子。


    // 按邮箱查询用户：登录、注册校验、找回流程常用。
    // 这个是 Spring Data JPA 具体实现的。声明返回 Optional<User> 后，框架查询数据库：
    // 查到用户 -> 返回 Optional.of(user)
    // 没查到 -> 返回 Optional.empty()
    Optional<User> findByEmail(String email);

    // 校验邮箱是否已存在：用于注册前唯一性检查。
    boolean existsByEmail(String email);

    // 按第三方平台返回的用户唯一标识查询：用于 OAuth 登录绑定场景。
    Optional<User> findByProviderUserId(String providerUserId);
}


// 我自己看的部分
// 为什么 Repository 没写 @Repository，因为它继承了 JpaRepository
// Spring Boot 启动时看到：extends JpaRepository
// 就知道这是 JPA Repository，我来自动创建 Bean；所以它自动注册进 IOC 容器。
// 等于 Spring 帮你隐式做了：
// @Repository
// public class UserRepositoryProxy implements UserRepository