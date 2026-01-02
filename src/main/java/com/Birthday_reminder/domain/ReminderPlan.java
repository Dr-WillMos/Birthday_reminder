package com.Birthday_reminder.domain;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * 
 * @TableName reminder_plan
 */
@Data
public class ReminderPlan {
    private Integer id;

    private Integer relationshipId;

    private LocalDate reminderDate;

    private Integer daysBefore;

    //通知类型；0-提醒用户；1-发送祝贺
    private Integer reminderType;

    //通知类型；0-待执行；1-已执行
    private Integer executionStatus;
}