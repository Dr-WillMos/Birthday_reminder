package com.Birthday_reminder.mapper;

import com.Birthday_reminder.domain.ReminderPlan;

import java.time.LocalDate;
import java.util.List;

/**
* @author willm
* @description 针对表【reminder_plan】的数据库操作Mapper
* @createDate 2025-12-14 14:38:30
* @Entity com.Birthday_reminder.domain.ReminderPlan
*/
public interface ReminderPlanMapper {

    void saveBatch(List<ReminderPlan> plans);

    void deleteByRelationshipId(Integer relationshipId);

    List<ReminderPlan> listUndoPlans(LocalDate now);

    void update(ReminderPlan plan);

    List<ReminderPlan> selectAll(Integer relationshipId);

    ReminderPlan selectById(Integer id);

    void insert(ReminderPlan reminderPlan);

    void deleteById(Integer id);
}



