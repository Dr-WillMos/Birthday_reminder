package com.Birthday_reminder.service;

import com.Birthday_reminder.domain.ReminderRecord;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ReminderRecordService {
    
    PageInfo<ReminderRecord> getPageList(Integer page, Integer size, Integer relationshipId);
    
    ReminderRecord getById(Integer id);
    
    void markAsProcessed(Integer id);
    
    /**
     * 获取待发送的提醒记录
     * @return 待发送的提醒记录列表
     */
    List<ReminderRecord> getPendingRecords();
}
