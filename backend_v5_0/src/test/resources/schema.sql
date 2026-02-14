-- ================================================================
-- 医院选择系统 - H2测试数据库初始化脚本
-- ================================================================

-- 用户权限模块
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    phone CHAR(11) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    gender TINYINT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_status ON sys_user(status);

CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, role_id)
);

-- 基础数据模块
DROP TABLE IF EXISTS area_info;
DROP TABLE IF EXISTS disease_type;

CREATE TABLE area_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    parent_code VARCHAR(20) NOT NULL,
    level TINYINT NOT NULL
);

CREATE INDEX idx_area_parent ON area_info(parent_code);

CREATE TABLE disease_type (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    disease_name VARCHAR(50) NOT NULL,
    disease_code VARCHAR(20) NOT NULL UNIQUE,
    sort INT DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_disease_parent ON disease_type(parent_id);

-- 医院医生模块
DROP TABLE IF EXISTS doctor_info;
DROP TABLE IF EXISTS hospital_department;
DROP TABLE IF EXISTS hospital_info;

CREATE TABLE hospital_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(100) NOT NULL UNIQUE,
    hospital_level VARCHAR(20) NOT NULL,
    province_code VARCHAR(20) NOT NULL,
    city_code VARCHAR(20) NOT NULL,
    area_code VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(50) DEFAULT NULL,
    key_departments VARCHAR(255) DEFAULT NULL,
    medical_equipment VARCHAR(255) DEFAULT NULL,
    expert_team TEXT DEFAULT NULL,
    intro TEXT DEFAULT NULL,
    rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INT NOT NULL DEFAULT 0,
    is_medical_insurance TINYINT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hospital_city ON hospital_info(city_code);
CREATE INDEX idx_hospital_level ON hospital_info(hospital_level);

CREATE TABLE hospital_department (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    hospital_id BIGINT NOT NULL,
    dept_name VARCHAR(50) NOT NULL,
    dept_intro TEXT DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_dept_hospital ON hospital_department(hospital_id);

CREATE TABLE doctor_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    doctor_name VARCHAR(50) NOT NULL,
    hospital_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    title VARCHAR(50) NOT NULL,
    specialty VARCHAR(255) DEFAULT NULL,
    academic_background VARCHAR(255) DEFAULT NULL,
    schedule_time VARCHAR(100) DEFAULT NULL,
    consultation_fee DECIMAL(10,2) DEFAULT NULL,
    rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctor_hospital ON doctor_info(hospital_id);
CREATE INDEX idx_doctor_dept ON doctor_info(dept_id);

-- 社区交流模块
DROP TABLE IF EXISTS community_like;
DROP TABLE IF EXISTS community_comment;
DROP TABLE IF EXISTS community_topic;

CREATE TABLE community_topic (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    disease_code VARCHAR(20) DEFAULT NULL,
    board_level1 VARCHAR(50) NOT NULL,
    board_level2 VARCHAR(50) DEFAULT NULL,
    board_type TINYINT NOT NULL DEFAULT 1,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    collect_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_topic_user ON community_topic(user_id);
CREATE INDEX idx_topic_board ON community_topic(board_level1);
CREATE INDEX idx_topic_time ON community_topic(create_time);

CREATE TABLE community_comment (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL DEFAULT 0,
    content VARCHAR(500) NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_comment_topic ON community_comment(topic_id);
CREATE INDEX idx_comment_parent ON community_comment(parent_id);

CREATE TABLE community_like (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type TINYINT NOT NULL,
    target_id BIGINT NOT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, target_type, target_id)
);

-- 用户数据模块
DROP TABLE IF EXISTS user_query_history;
DROP TABLE IF EXISTS user_medical_history;
DROP TABLE IF EXISTS user_message;
DROP TABLE IF EXISTS user_collection;

CREATE TABLE user_collection (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type TINYINT NOT NULL,
    target_id BIGINT NOT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, target_type, target_id)
);

CREATE TABLE user_message (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_message_receiver ON user_message(receiver_id);
CREATE INDEX idx_message_read ON user_message(receiver_id, is_read, create_time DESC);

CREATE TABLE user_medical_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    disease_name VARCHAR(100) NOT NULL,
    diagnosis_date DATE DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medical_user ON user_medical_history(user_id);

CREATE TABLE user_query_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    query_type TINYINT NOT NULL,
    target_id BIGINT DEFAULT NULL,
    query_params TEXT DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_query_user ON user_query_history(user_id);

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code) VALUES
('普通用户', 'user'),
('管理员', 'admin'),
('版主', 'moderator');

-- 管理员账号（密码：admin123）
INSERT INTO sys_user (phone, password, nickname, gender, status) VALUES
('13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1);

-- 管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 2);

-- 省市区数据
INSERT INTO area_info (code, name, parent_code, level) VALUES
('440000', '广东省', '0', 1),
('440100', '广州市', '440000', 2),
('440300', '深圳市', '440000', 2),
('440103', '荔湾区', '440100', 3),
('440104', '越秀区', '440100', 3),
('440105', '海珠区', '440100', 3),
('440106', '天河区', '440100', 3),
('440303', '罗湖区', '440300', 3),
('440304', '福田区', '440300', 3),
('440305', '南山区', '440300', 3);

-- 疾病分类
INSERT INTO disease_type (parent_id, disease_name, disease_code, sort) VALUES
(0, '心血管疾病', 'cardiovascular', 100),
(0, '内分泌疾病', 'endocrine', 90),
(0, '呼吸系统疾病', 'respiratory', 80),
(1, '高血压', 'hypertension', 10),
(1, '冠心病', 'coronary', 9),
(2, '糖尿病', 'diabetes', 10),
(3, '肺炎', 'pneumonia', 10);

-- 示例医院
INSERT INTO hospital_info (hospital_name, hospital_level, province_code, city_code, area_code, address, phone, key_departments, intro, rating, review_count, is_medical_insurance) VALUES
('广东省人民医院', 'grade3A', '440000', '440100', '440104', '广州市越秀区中山二路106号', '020-83827812', '心血管内科,肿瘤科', '广东省最大的综合性医院之一', 4.8, 2563, 1),
('中山大学附属第一医院', 'grade3A', '440000', '440100', '440104', '广州市越秀区中山二路58号', '020-87755766', '肝胆外科,泌尿外科', '国家三级甲等综合医院', 4.7, 3125, 1),
('广州市第一人民医院', 'grade3A', '440000', '440100', '440103', '广州市荔湾区盘福路1号', '020-81048888', '消化内科,呼吸内科', '市属最大的三级甲等综合医院', 4.5, 1823, 1);

-- 示例科室
INSERT INTO hospital_department (hospital_id, dept_name, dept_intro) VALUES
(1, '心血管内科', '国家临床重点专科'),
(1, '肿瘤科', '肿瘤综合治疗中心'),
(2, '肝胆外科', '国家重点学科'),
(2, '泌尿外科', '微创泌尿外科'),
(3, '消化内科', '广东省临床重点专科'),
(3, '呼吸内科', '呼吸系统疾病诊治');

-- 示例医生
INSERT INTO doctor_info (doctor_name, hospital_id, dept_id, title, specialty, schedule_time, consultation_fee, rating, review_count) VALUES
('张医生', 1, 1, '主任医师', '冠心病介入治疗', '周一上午、周三下午', 50.00, 4.8, 156),
('李医生', 1, 1, '副主任医师', '高血压诊治', '周二上午、周四下午', 30.00, 4.6, 98),
('王医生', 2, 4, '主任医师', '肝胆外科治疗', '周一下午、周五上午', 80.00, 4.9, 312);

-- 测试用户（密码：Test123456）
-- 注意：ID=1 是管理员，所以测试用户从 ID=2 开始
INSERT INTO sys_user (id, phone, password, nickname, gender, status) VALUES
(2, '13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户1', 1, 1),
(3, '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户2', 0, 1);

-- 测试用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(2, 1),
(3, 1);

-- 消息测试用户（密码：Test123456）
INSERT INTO sys_user (id, phone, password, nickname, gender, status) VALUES
(10, '13900000010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '消息测试用户1', 1, 1),
(11, '13900000011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '消息测试用户2', 0, 1);

-- 消息测试用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(10, 1),
(11, 1);

-- 示例病史记录（属于测试用户 ID=2）
INSERT INTO user_medical_history (id, user_id, disease_name, diagnosis_date, status) VALUES
(1, 2, '高血压', '2024-01-15', 1),
(2, 2, '糖尿病', '2024-02-20', 2);

-- 示例查询历史记录（属于测试用户 ID=2）
INSERT INTO user_query_history (id, user_id, query_type, target_id, query_params) VALUES
(1, 2, 1, 1, '{"province":"440000","city":"440100","level":"grade3A"}'),
(2, 2, 2, 1, '{"disease":"cardiovascular","province":"440000"}'),
(3, 2, 1, 2, '{"province":"440000","city":"440300"}');
