package com.Birthday_reminder.service;


import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.domain.ReminderPlan;
import com.github.pagehelper.PageInfo;

import java.time.LocalDate;
import java.util.List;

public interface RelationPlanService {

    void plan(Relationship relationship);

    void deleteByRelationshipId(Integer relationship);

    List<ReminderPlan> listUndoPlans(LocalDate now);

    void update(ReminderPlan plan);

    void executePlan(LocalDate now);

    PageInfo<ReminderPlan> getPageList(Integer page, Integer size, Integer relationshipId);

    ReminderPlan getById(Integer id);

    void create(ReminderPlan reminderPlan);

    void deleteById(Integer id);
}
