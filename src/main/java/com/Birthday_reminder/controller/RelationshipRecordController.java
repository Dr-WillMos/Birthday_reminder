package com.Birthday_reminder.controller;

import com.Birthday_reminder.domain.ReminderRecord;
import com.Birthday_reminder.service.ReminderRecordService;
import com.Birthday_reminder.vo.PageVO;
import com.Birthday_reminder.vo.ResponseEntity;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminder-records")
public class RelationshipRecordController {

    @Autowired
    private ReminderRecordService reminderRecordService;

    /**
     * 获取提醒记录列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageVO<ReminderRecord>> getList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer relationshipId) {
        PageInfo<ReminderRecord> pageInfo = reminderRecordService.getPageList(page + 1, size, relationshipId);
        PageVO<ReminderRecord> pageVO = new PageVO<>(
                pageInfo.getList(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal()
        );
        return ResponseEntity.success(pageVO);
    }

    /**
     * 根据ID获取提醒记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReminderRecord> getById(@PathVariable Integer id) {
        ReminderRecord record = reminderRecordService.getById(id);
        return ResponseEntity.success(record);
    }

    /**
     * 标记提醒记录为已处理
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<Void> markAsProcessed(@PathVariable Integer id) {
        reminderRecordService.markAsProcessed(id);
        return ResponseEntity.success();
    }
}
