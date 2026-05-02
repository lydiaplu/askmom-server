USE maternal_service_db;

CREATE TABLE maternal_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,

    due_date DATE, -- 预产期，用来判断是否还在孕期和怀孕的周数
    delivery_date DATE, -- 实际生产日期，如果不为空则为已经为产后
    baby_count INT NOT NULL DEFAULT 1,

    maternal_stage VARCHAR(30),
    -- trying_to_conceive = 备孕, pregnant = 怀孕中, postpartum = 产后, parenting = 在育儿阶段
    delivery_type VARCHAR(50),
    -- vaginal = 自然阴道分娩, c_section = 剖腹产, assisted_vaginal = 辅助阴道分娩

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
