package com.hopeonllc.askmom_user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// @Entity：告诉 JPA“这是一个要映射到数据库表的实体类”。
// 没有它，Spring Data JPA 不会把这个类当成数据库对象来管理。
@Entity
// @Table(name = "users")：指定这 个实体对应的数据库表名是 users。
// name = "users"：显式绑定表名，避免类名和表名推断不一致。
@Table(name = "users")
// @Getter：由 Lombok 自动为所有字段生成 getter 方法，减少样板代码。
@Getter
// @Setter：由 Lombok 自动为所有字段生成 setter 方法，减少样板代码。
@Setter
// @NoArgsConstructor：自动生成无参构造器。
// JPA 在查询/反射创建实体对象时通常需要无参构造器。
@NoArgsConstructor
public class User {

    // @Id：声明这是主键字段（每条数据的唯一标识）。
    @Id
    // @GeneratedValue：主键值由数据库自动生成。
    // strategy = GenerationType.IDENTITY：使用 MySQL 自增列（AUTO_INCREMENT）策略。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column：定义字段和表列的映射规则。
    // name = "email"：明确该字段对应数据库中的 email 列。
    // nullable = false：该列不允许为 NULL（必须有值）。
    // unique = true：该列必须唯一，不能出现重复值。
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email length cannot exceed 255")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // name = "password_hash"：明确该字段对应数据库中的 password_hash 列。
    @NotBlank(message = "Password hash cannot be empty")
    @Size(max = 255, message = "Password hash length cannot exceed 255")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // name = "created_at"：明确该字段对应数据库中的 created_at 列。
    // insertable = false：执行 INSERT 时不写入该列，交给数据库默认值（CURRENT_TIMESTAMP）。
    // updatable = false：执行 UPDATE 时不更新该列，保证创建时间一旦生成就不被改动。
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // name = "updated_at"：明确该字段对应数据库中的 updated_at 列。
    // 时间由数据库维护（ON UPDATE CURRENT_TIMESTAMP）。
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // name = "is_active"：明确该字段对应数据库中的 is_active 列。
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // 这里只指定列名；未写 nullable，表示沿用 JPA 默认可空（与 SQL 定义可空一致）。
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // name = "profile_completed"：明确该字段对应数据库中的 profile_completed 列。
    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted = false;

    // name = "email_verified"：明确该字段对应数据库中的 email_verified 列。
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    // 这里只指定列名；该时间允许为空（例如尚未完成邮箱验证）。
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    // name = "auth_provider"：明确该字段对应数据库中的 auth_provider 列。
    // length = 50：生成/校验字段长度上限为 50，和数据库 VARCHAR(50) 对齐。
    @Pattern(regexp = "^(email|google|apple)$", message = "Auth provider must be email, google or apple")
    @Size(max = 50, message = "Auth provider length cannot exceed 50")
    @Column(name = "auth_provider", length = 50)
    private String authProvider = "email";

    // name = "provider_user_id"：明确该字段对应数据库中的 provider_user_id 列。
    // 该字段保存第三方平台返回的用户唯一标识，可为空。
    @Size(max = 255, message = "Provider user id length cannot exceed 255")
    @Column(name = "provider_user_id")
    private String providerUserId;
}
