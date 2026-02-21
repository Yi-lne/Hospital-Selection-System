-- 添加用户封禁时间字段
-- 执行此SQL前请确保备份数据库

ALTER TABLE sys_user
ADD COLUMN ban_start_time DATETIME DEFAULT NULL COMMENT '封禁开始时间' AFTER status,
ADD COLUMN ban_end_time DATETIME DEFAULT NULL COMMENT '封禁结束时间（NULL表示永久封禁）' AFTER ban_start_time,
ADD COLUMN ban_reason VARCHAR(500) DEFAULT NULL COMMENT '封禁原因' AFTER ban_end_time;
