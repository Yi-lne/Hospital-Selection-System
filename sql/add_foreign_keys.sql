-- ================================================================
-- 医院选择系统 - 添加外键约束脚本
--
-- 注意事项：
-- 1. 执行此脚本前请确保数据一致性（无孤儿数据）
-- 2. 建议在低峰期执行，外键检查可能影响性能
-- 3. 执行前请备份数据库
--
-- 外键策略：
-- - ON DELETE RESTRICT: 阻止删除有关联数据的记录（需要先删除子数据）
-- - 这保证了数据完整性，同时不影响系统现有功能
-- ================================================================

USE `hospital_selection`;

-- ================================================================
-- 第一部分：用户权限模块外键
-- ================================================================

-- sys_user_role.user_id -> sys_user.id
ALTER TABLE `sys_user_role`
ADD CONSTRAINT `fk_user_role_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- sys_user_role.role_id -> sys_role.id
ALTER TABLE `sys_user_role`
ADD CONSTRAINT `fk_user_role_role`
FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- ================================================================
-- 第二部分：医院医生模块外键
-- ================================================================

-- hospital_department.hospital_id -> hospital_info.id
ALTER TABLE `hospital_department`
ADD CONSTRAINT `fk_dept_hospital`
FOREIGN KEY (`hospital_id`) REFERENCES `hospital_info`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- doctor_info.hospital_id -> hospital_info.id
ALTER TABLE `doctor_info`
ADD CONSTRAINT `fk_doctor_hospital`
FOREIGN KEY (`hospital_id`) REFERENCES `hospital_info`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- doctor_info.dept_id -> hospital_department.id
ALTER TABLE `doctor_info`
ADD CONSTRAINT `fk_doctor_dept`
FOREIGN KEY (`dept_id`) REFERENCES `hospital_department`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- ================================================================
-- 第三部分：社区交流模块外键
-- ================================================================

-- community_topic.user_id -> sys_user.id
ALTER TABLE `community_topic`
ADD CONSTRAINT `fk_topic_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- community_comment.topic_id -> community_topic.id
ALTER TABLE `community_comment`
ADD CONSTRAINT `fk_comment_topic`
FOREIGN KEY (`topic_id`) REFERENCES `community_topic`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- community_comment.user_id -> sys_user.id
ALTER TABLE `community_comment`
ADD CONSTRAINT `fk_comment_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- community_like.user_id -> sys_user.id
ALTER TABLE `community_like`
ADD CONSTRAINT `fk_like_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- community_report.user_id -> sys_user.id
ALTER TABLE `community_report`
ADD CONSTRAINT `fk_report_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- community_report.handler_id -> sys_user.id (可以为NULL)
ALTER TABLE `community_report`
ADD CONSTRAINT `fk_report_handler`
FOREIGN KEY (`handler_id`) REFERENCES `sys_user`(`id`)
ON DELETE SET NULL ON UPDATE CASCADE;

-- ================================================================
-- 第四部分：用户数据模块外键
-- ================================================================

-- user_collection.user_id -> sys_user.id
ALTER TABLE `user_collection`
ADD CONSTRAINT `fk_collection_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- user_notification.user_id -> sys_user.id
ALTER TABLE `user_notification`
ADD CONSTRAINT `fk_notification_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- user_medical_history.user_id -> sys_user.id
ALTER TABLE `user_medical_history`
ADD CONSTRAINT `fk_medical_history_user`
FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- ================================================================
-- 完成提示
-- ================================================================
SELECT '外键约束添加完成！' AS message;
