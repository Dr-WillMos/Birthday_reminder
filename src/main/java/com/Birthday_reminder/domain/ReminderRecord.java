package com.Birthday_reminder.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName reminder_record
 */
@Data
public class ReminderRecord implements Serializable {
    private Integer id;

    private Integer relationshipId;

    private LocalDateTime reminderTime;

    private String receiver;

    private Integer reminderType;
    
    // 关联的亲友信息（非数据库字段，用于查询）
    private String relationshipName;
    
    private String birthdayDate;
    
    private Integer daysBeforeReminder;
    
    // 祝贺相关字段（非数据库字段）
    private String greeting;
    
    private String selfCall;

    private static final long serialVersionUID = 1L;
}