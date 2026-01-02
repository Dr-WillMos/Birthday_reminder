package com.Birthday_reminder.controller;

import com.github.pagehelper.PageInfo;
import com.Birthday_reminder.dto.RelationshipPageDTO;
import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.domain.ReminderPlan;
import com.Birthday_reminder.service.RelationPlanService;
import com.Birthday_reminder.service.RelationshipService;
import com.Birthday_reminder.vo.PageVO;
import com.Birthday_reminder.vo.RelationshipPageVO;
import com.Birthday_reminder.vo.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminders")
public class RelationshipReminderController {

    @Autowired
    private RelationPlanService relationPlanService;

    /**
     * 获取提醒计划列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageVO<ReminderPlan>> getList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer relationshipId) {
        PageInfo<ReminderPlan> pageInfo = relationPlanService.getPageList(page + 1, size, relationshipId);
        PageVO<ReminderPlan> pageVO = new PageVO<>(
                pageInfo.getList(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal()
        );
        return ResponseEntity.success(pageVO);
    }

    /**
     * 根据ID获取提醒计划
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReminderPlan> getById(@PathVariable Integer id) {
        ReminderPlan plan = relationPlanService.getById(id);
        return ResponseEntity.success(plan);
    }

    /**
     * 创建提醒计划
     */
    @PostMapping
    public ResponseEntity<ReminderPlan> create(@RequestBody ReminderPlan reminderPlan) {
        try {
            relationPlanService.create(reminderPlan);
            return ResponseEntity.success(reminderPlan);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.fail("创建提醒计划失败: " + e.getMessage());
        }
    }

    /**
     * 更新提醒计划
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody ReminderPlan reminderPlan) {
        reminderPlan.setId(id);
        relationPlanService.update(reminderPlan);
        return ResponseEntity.success();
    }

    /**
     * 删除提醒计划
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        relationPlanService.deleteById(id);
        return ResponseEntity.success();
    }
}
