
# 亲友关系表
-- table_birthday.relationship definition

CREATE TABLE `relationship` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `name` varchar(100) NOT NULL,
                                `my_email` varchar(100) DEFAULT NULL,
                                `calendar_type` int DEFAULT '0',
                                `birthday` date DEFAULT NULL,
                                `birthday_month` int DEFAULT NULL,
                                `birthday_day` int DEFAULT NULL,
                                `tag` varchar(50) DEFAULT NULL,
                                `reminder_enabled` int DEFAULT '0',
                                `congratulate_enabled` int DEFAULT '0',
                                `relationship_email` varchar(100) DEFAULT NULL,
                                `greeting` varchar(200) DEFAULT NULL,
                                `self_call` varchar(50) DEFAULT NULL,
                                `notes` text,
                                `days_before` json DEFAULT NULL,
                                `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
# 提醒计划表
# 该表用于规划后面的发送邮件等任务
-- table_birthday.reminder_plan definition

CREATE TABLE `reminder_plan` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT '计划ID，主键',
                                 `relationship_id` int NOT NULL COMMENT '关联的亲友关系ID',
                                 `reminder_date` date DEFAULT NULL COMMENT '提醒日期',
                                 `days_before` int NOT NULL COMMENT '提前提醒天数',
                                 `reminder_type` int DEFAULT '0' COMMENT '提醒类型 (0:生日提醒, 1:其他提醒)',
                                 `execution_status` int DEFAULT '0' COMMENT '执行状态 (0:待执行, 1:已执行, 2:已取消)',
                                 `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `relationship_id` (`relationship_id`),
                                 CONSTRAINT `reminder_plan_ibfk_1` FOREIGN KEY (`relationship_id`) REFERENCES `relationship` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提醒计划表';
# 提醒记录表
# 记录，用于保存系统日志供日后查询
-- table_birthday.reminder_record definition

CREATE TABLE `reminder_record` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
                                   `relationship_id` int NOT NULL COMMENT '关联的亲友关系ID',
                                   `reminder_time` datetime NOT NULL COMMENT '实际提醒时间',
                                   `receiver` varchar(100) DEFAULT NULL COMMENT '提醒接收方',
                                   `reminder_type` int DEFAULT NULL COMMENT '提醒类型',
                                   `reminder_content` text COMMENT '提醒内容',
                                   `send_status` int DEFAULT '0' COMMENT '发送状态 (0:成功, 1:失败)',
                                   `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   KEY `relationship_id` (`relationship_id`),
                                   CONSTRAINT `reminder_record_ibfk_1` FOREIGN KEY (`relationship_id`) REFERENCES `relationship` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提醒记录表';
