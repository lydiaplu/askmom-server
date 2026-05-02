USE baby_service_db;

CREATE TABLE babies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,

    first_name VARCHAR(100),
    last_name VARCHAR(100),
    middle_name VARCHAR(100),
    nickname VARCHAR(100), -- 有人不喜欢提供宝宝姓名，可以提供昵称

    gender VARCHAR(20),
    birth_date DATE,

    feeding_type ENUM('breastfeeding', 'formula', 'mixed', 'unknown'),
    -- breastfeeding = 母乳喂养 / formula = 奶粉喂养 / mixed = 混合喂养

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
