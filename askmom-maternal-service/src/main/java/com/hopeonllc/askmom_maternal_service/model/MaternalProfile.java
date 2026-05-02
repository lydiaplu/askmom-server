package com.hopeonllc.askmom_maternal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// @Entity：告诉 JPA“这是一个要映射到数据库表的实体类”。
// 没有它，Spring Data JPA 不会把这个类当成数据库对象来管理。
@Entity
// @Table(name = "maternal_profiles")：指定这个实体对应的数据库表名是 maternal_profiles。
// name = "maternal_profiles"：显式绑定表名，避免类名和表名推断不一致。
@Table(name = "maternal_profiles")
// @Getter：由 Lombok 自动为所有字段生成 getter 方法，减少样板代码。
@Getter
// @Setter：由 Lombok 自动为所有字段生成 setter 方法，减少样板代码。
@Setter
// @NoArgsConstructor：自动生成无参构造器。
// JPA 在查询/反射创建实体对象时通常需要无参构造器。
@NoArgsConstructor
public class MaternalProfile {

    // @Id：声明这是主键字段（每条数据的唯一标识）。
    @Id
    // @GeneratedValue：主键值由数据库自动生成。
    // strategy = GenerationType.IDENTITY：使用 MySQL 自增列（AUTO_INCREMENT）策略。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column：定义字段和表列的映射规则。
    // name = "user_id"：明确该字段对应数据库中的 user_id 列。
    // nullable = false：该列不允许为 NULL（必须有值）。
    // unique = true：该列必须唯一，一个用户只能有一条 maternal profile。
    @NotNull(message = "User id cannot be null")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // name = "due_date"：明确该字段对应数据库中的 due_date 列。
    // 预产期，用来判断是否还在孕期和怀孕的周数。
    @Column(name = "due_date")
    private LocalDate dueDate;

    // name = "delivery_date"：明确该字段对应数据库中的 delivery_date 列。
    // 实际生产日期；如果不为空，则表示用户已经进入产后阶段。
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    // name = "baby_count"：明确该字段对应数据库中的 baby_count 列。
    // nullable = false：该列不允许为 NULL。
    // babyCount 默认值为 1，表示默认是一胎/一个宝宝。
    @NotNull(message = "Baby count cannot be null")
    @Min(value = 1, message = "Baby count must be greater than or equal to 1")
    @Column(name = "baby_count", nullable = false)
    private Integer babyCount = 1;

    // name = "maternal_stage"：明确该字段对应数据库中的 maternal_stage 列。
    // length = 30：生成/校验字段长度上限为 30，和数据库 VARCHAR(30) 对齐。
    // trying_to_conceive = 备孕, pregnant = 怀孕中, postpartum = 产后, parenting = 在育儿阶段。
    @Pattern(
            regexp = "^(trying_to_conceive|pregnant|postpartum|parenting)$",
            message = "Maternal stage must be trying_to_conceive, pregnant, postpartum or parenting"
    )
    @Size(max = 30, message = "Maternal stage length cannot exceed 30")
    @Column(name = "maternal_stage", length = 30)
    private String maternalStage;

    // name = "delivery_type"：明确该字段对应数据库中的 delivery_type 列。
    // length = 50：生成/校验字段长度上限为 50，和数据库 VARCHAR(50) 对齐。
    // vaginal = 自然阴道分娩, c_section = 剖腹产, assisted_vaginal = 辅助阴道分娩。
    @Pattern(
            regexp = "^(vaginal|c_section|assisted_vaginal)$",
            message = "Delivery type must be vaginal, c_section or assisted_vaginal"
    )
    @Size(max = 50, message = "Delivery type length cannot exceed 50")
    @Column(name = "delivery_type", length = 50)
    private String deliveryType;

    // name = "created_at"：明确该字段对应数据库中的 created_at 列。
    // insertable = false：执行 INSERT 时不写入该列，交给数据库默认值（CURRENT_TIMESTAMP）。
    // updatable = false：执行 UPDATE 时不更新该列，保证创建时间一旦生成就不被改动。
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // name = "updated_at"：明确该字段对应数据库中的 updated_at 列。
    // 时间由数据库维护（ON UPDATE CURRENT_TIMESTAMP）。
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
