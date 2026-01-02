package com.Birthday_reminder.mapper;

import com.Birthday_reminder.domain.ReminderRecord;

import java.util.List;

/**
* @author willm
* @description 针对表【reminder_record】的数据库操作Mapper
* @createDate 2025-12-14 14:38:30
* @Entity com.Birthday_reminder.domain.ReminderRecord
*/
public interface ReminderRecordMapper {

    List<ReminderRecord> selectAll(Integer relationshipId);

    ReminderRecord selectById(Integer id);

    void insert(ReminderRecord record);
    
    /**
     * 查询未发送的提醒记录（用于邮件发送任务）
     * @return 未发送的提醒记录列表（包含关联的亲友信息）
     */
    List<ReminderRecord> selectPendingRecords();
}




