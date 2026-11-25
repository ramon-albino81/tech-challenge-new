CREATE TABLE app_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    login VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_type VARCHAR(32) NOT NULL,
    last_modified_at DATETIME(6) NOT NULL,
    address JSON NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uq_app_user_email UNIQUE (email),
    CONSTRAINT uq_app_user_login UNIQUE (login),
    CONSTRAINT ck_app_user_type CHECK (user_type IN ('DONO_RESTAURANTE', 'CLIENTE')),
    CONSTRAINT ck_app_user_address CHECK (
        JSON_TYPE(address) = 'OBJECT'
        AND JSON_LENGTH(address) = 4
        AND JSON_TYPE(JSON_EXTRACT(address, '$.street')) = 'STRING'
        AND CHAR_LENGTH(TRIM(JSON_UNQUOTE(JSON_EXTRACT(address, '$.street')))) > 0
        AND JSON_TYPE(JSON_EXTRACT(address, '$.number')) = 'STRING'
        AND CHAR_LENGTH(TRIM(JSON_UNQUOTE(JSON_EXTRACT(address, '$.number')))) > 0
        AND JSON_TYPE(JSON_EXTRACT(address, '$.city')) = 'STRING'
        AND CHAR_LENGTH(TRIM(JSON_UNQUOTE(JSON_EXTRACT(address, '$.city')))) > 0
        AND JSON_TYPE(JSON_EXTRACT(address, '$.postal_code')) = 'STRING'
        AND CHAR_LENGTH(TRIM(JSON_UNQUOTE(JSON_EXTRACT(address, '$.postal_code')))) > 0
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
