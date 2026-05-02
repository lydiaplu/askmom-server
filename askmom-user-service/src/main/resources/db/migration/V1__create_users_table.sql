USE user_service_db;

CREATE TABLE users (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   email VARCHAR(255) NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,

   profile_completed BOOLEAN NOT NULL DEFAULT FALSE, -- 用户有没有填完资料

   email_verified BOOLEAN NOT NULL DEFAULT FALSE, -- 邮箱验证是否成功
   email_verified_at DATETIME,

   auth_provider VARCHAR(50) DEFAULT 'email', -- google / apple / email
   provider_user_id VARCHAR(255), -- 第三方返回的唯一ID

   is_active BOOLEAN NOT NULL DEFAULT TRUE,
   last_login_at DATETIME,

   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   created_by BIGINT NULL,

   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   updated_by BIGINT NULL,

   deleted_at DATETIME NULL,
   deleted_by BIGINT NULL
);