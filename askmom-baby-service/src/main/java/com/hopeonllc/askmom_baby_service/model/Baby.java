package com.hopeonllc.askmom_baby_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// @Entity：告诉 JPA“这是一个要映射到数据库表的实体类”。
// 没有它，Spring Data JPA 不会把这个类当成数据库对象来管理。
@Entity
// @Table(name = "babies")：指定这个实体对应的数据库表名是 babies。
// name = "babies"：显式绑定表名，避免类名和表名推断不一致。
@Table(name = "babies")
// @Getter：由 Lombok 自动为所有字段生成 getter 方法，减少样板代码。
@Getter
// @Setter：由 Lombok 自动为所有字段生成 setter 方法，减少样板代码。
@Setter
// @NoArgsConstructor：自动生成无参构造器。
// JPA 在查询/反射创建实体对象时通常需要无参构造器。
@NoArgsConstructor
public class Baby {

    // @Id：声明这是主键字段（每条数据的唯一标识）。
    @Id
    // @GeneratedValue：主键值由数据库自动生成。
    // strategy = GenerationType.IDENTITY：使用 MySQL 自增列（AUTO_INCREMENT）策略。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column：定义字段和表列的映射规则。
    // name = "user_id"：明确该字段对应数据库中的 user_id 列。
    // nullable = false：该列不允许为 NULL（必须有值）。
    // baby-service 允许同一个 user_id 对应多个宝宝，所以这里不加 unique。
    @NotNull(message = "User id cannot be null")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // name = "first_name"：明确该字段对应数据库中的 first_name 列。
    // length = 100：生成/校验字段长度上限为 100，和数据库 VARCHAR(100) 对齐。
    @Size(max = 100, message = "First name length cannot exceed 100")
    @Column(name = "first_name", length = 100)
    private String firstName;

    // name = "last_name"：明确该字段对应数据库中的 last_name 列。
    // length = 100：生成/校验字段长度上限为 100，和数据库 VARCHAR(100) 对齐。
    @Size(max = 100, message = "Last name length cannot exceed 100")
    @Column(name = "last_name", length = 100)
    private String lastName;

    // name = "middle_name"：明确该字段对应数据库中的 middle_name 列。
    // length = 100：生成/校验字段长度上限为 100，和数据库 VARCHAR(100) 对齐。
    @Size(max = 100, message = "Middle name length cannot exceed 100")
    @Column(name = "middle_name", length = 100)
    private String middleName;

    // name = "nickname"：明确该字段对应数据库中的 nickname 列。
    // 有人不喜欢提供宝宝姓名，可以提供昵称。
    @Size(max = 100, message = "Nickname length cannot exceed 100")
    @Column(name = "nickname", length = 100)
    private String nickname;

    // name = "gender"：明确该字段对应数据库中的 gender 列。
    // length = 20：生成/校验字段长度上限为 20，和数据库 VARCHAR(20) 对齐。
    @Size(max = 20, message = "Gender length cannot exceed 20")
    @Column(name = "gender", length = 20)
    private String gender;

    // name = "birth_date"：明确该字段对应数据库中的 birth_date 列。
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // name = "feeding_type"：明确该字段对应数据库中的 feeding_type 列。
    // breastfeeding = 母乳喂养 / formula = 奶粉喂养 / mixed = 混合喂养 / unknown = 未知。
    @Enumerated(EnumType.STRING)
    @Column(name = "feeding_type", columnDefinition = "ENUM('breastfeeding','formula','mixed','unknown')")
    private FeedingType feedingType;

    // name = "created_at"：明确该字段对应数据库中的 created_at 列。
    // insertable = false：执行 INSERT 时不写入该列，交给数据库默认值（CURRENT_TIMESTAMP）。
    // updatable = false：执行 UPDATE 时不更新该列，保证创建时间一旦生成就不被改动。
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // name = "updated_at"：明确该字段对应数据库中的 updated_at 列。
    // 时间由数据库维护（ON UPDATE CURRENT_TIMESTAMP）。
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum FeedingType {
        breastfeeding,
        formula,
        mixed,
        unknown
    }
}
