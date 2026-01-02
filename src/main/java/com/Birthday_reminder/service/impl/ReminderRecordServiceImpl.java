package com.Birthday_reminder.service.impl;

import com.Birthday_reminder.domain.ReminderRecord;
import com.Birthday_reminder.mapper.ReminderRecordMapper;
import com.Birthday_reminder.service.ReminderRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderRecordServiceImpl implements ReminderRecordService {

    @Autowired
    private ReminderRecordMapper mapper;

    @Override
    public PageInfo<ReminderRecord> getPageList(Integer page, Integer size, Integer relationshipId) {
        PageHelper.startPage(page, size);
        List<ReminderRecord> list = mapper.selectAll(relationshipId);
        return new PageInfo<>(list);
    }

    @Override
    public ReminderRecord getById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public void markAsProcessed(Integer id) {
        // 未实装
    }
    
    @Override
    public List<ReminderRecord> getPendingRecords()
    {
        return mapper.selectPendingRecords();
    }
}
