package com.Birthday_reminder.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.domain.ReminderPlan;
import com.Birthday_reminder.domain.ReminderRecord;
import com.Birthday_reminder.mapper.RelationshipMapper;
import com.Birthday_reminder.mapper.ReminderPlanMapper;
import com.Birthday_reminder.mapper.ReminderRecordMapper;
import com.Birthday_reminder.service.RelationPlanService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RelationPlanServiceImpl implements RelationPlanService {

    @Autowired
    private ReminderPlanMapper mapper;
    
    @Autowired
    private ReminderRecordMapper reminderRecordMapper;
    
    @Autowired
    private RelationshipMapper relationshipMapper;
    @Override
    public void plan(Relationship relationship) {
        if (Objects.isNull(relationship)){
            return;
        }
        // 删除旧计划
        mapper.deleteByRelationshipId(relationship.getId());
        
        // 验证参数
        validateParams(relationship);
        
        // 计算生日
        LocalDate birthday = calculateBirthday(relationship);
        LocalDate now = LocalDate.now();
        List<ReminderPlan> plans = new ArrayList<>();
        
        // 1. 制定提醒用户的计划（reminderType=0）
        if (relationship.getRemindEnabled() != null && relationship.getRemindEnabled() == 1) {
            List<Integer> daysBefore = relationship.getDaysBefore();
            if (CollectionUtil.isNotEmpty(daysBefore)) {
                for (Integer bf : daysBefore) {
                    LocalDate reminderDate = birthday.minusDays(bf);
                    if (now.isAfter(reminderDate)) {
                        continue;
                    }
                    ReminderPlan plan = buildBasicPlan(relationship);
                    plan.setReminderType(0);  // 0-提醒用户
                    plan.setReminderDate(reminderDate);
                    plan.setDaysBefore(bf);
                    plans.add(plan);
                }
            }
        }
        
        // reminderType=1
        if (relationship.getCongratulateEnabled() != null && relationship.getCongratulateEnabled() == 1) {
            // 验证亲友邮箱
            if (StrUtil.isBlank(relationship.getRelationshipEmail())) {
                log.warn("亲友 {} 启用了祝贺功能但没有设置邮箱，跳过祝贺计划", relationship.getName());
            } else {
                // 在生日当天发送祝贺
                if (!now.isAfter(birthday)) {
                    ReminderPlan congratulatePlan = buildBasicPlan(relationship);
                    congratulatePlan.setReminderType(1);  // 1-发送祝贺
                    congratulatePlan.setReminderDate(birthday);
                    congratulatePlan.setDaysBefore(0);  // 当天发送
                    plans.add(congratulatePlan);
                }
            }
        }
        
        if (CollectionUtil.isNotEmpty(plans)) {
            mapper.saveBatch(plans);
            log.info("为亲友 {} 创建了 {} 条计划", relationship.getName(), plans.size());
        }
    }

    @Override
    public void deleteByRelationshipId(Integer relationship) {
        mapper.deleteByRelationshipId(relationship);
    }

    @Override
    public List<ReminderPlan> listUndoPlans(LocalDate now) {
        return mapper.listUndoPlans(now);
    }

    @Override
    public void update(ReminderPlan plan) {
        mapper.update(plan);
    }

    @Override
    @Transactional
    public void executePlan(LocalDate now) {
        List<ReminderPlan> plans = mapper.listUndoPlans(now);
        if (CollUtil.isEmpty(plans)) {
            log.info("当前没有执行计划");
            return;
        }
        
        log.info("找到 {} 条待执行的提醒计划", plans.size());
        
        for (ReminderPlan plan : plans) {
            try {
                log.info("开始执行计划：{}", plan);
                
                // 获取关联的亲友信息
                Relationship relationship = relationshipMapper.selectById(plan.getRelationshipId());
                if (relationship == null) {
                    log.warn("计划ID {} 关联的亲友信息不存在，跳过", plan.getId());
                    continue;
                }
                
                // 检查邮箱是否存在
                if (StrUtil.isBlank(relationship.getMyEmail())) {
                    log.warn("计划ID {} 关联的亲友 {} 没有邮箱，跳过", plan.getId(), relationship.getName());
                    continue;
                }
                
                // 创建提醒记录
                ReminderRecord record = new ReminderRecord();
                record.setRelationshipId(plan.getRelationshipId());
                record.setReminderTime(LocalDateTime.now());
                record.setReminderType(plan.getReminderType());
                
                // 根据 reminderType 设置不同的接收者
                if (plan.getReminderType() == 0) {
                    // 0-提醒用户，发送到用户邮箱
                    record.setReceiver(relationship.getMyEmail());
                } else if (plan.getReminderType() == 1) {
                    // 1-发送祝贺，发送到亲友邮箱
                    record.setReceiver(relationship.getRelationshipEmail());
                }
                
                // 保存提醒记录
                reminderRecordMapper.insert(record);
                log.info("已创建提醒记录：亲友 {}, 接收者 {}", relationship.getName(), relationship.getMyEmail());
                
                // 更新计划状态为已执行
                plan.setExecutionStatus(1);
                mapper.update(plan);
                log.info("计划ID {} 执行完成", plan.getId());
                
            } catch (Exception e) {
                log.error("执行计划ID {} 失败: {}", plan.getId(), e.getMessage(), e);
            }
        }
        
        log.info("提醒计划执行完毕，共处理 {} 条计划", plans.size());
    }

    private ReminderPlan buildBasicPlan(Relationship ship) {
        ReminderPlan plan = new ReminderPlan();
        plan.setRelationshipId(ship.getId());
        plan.setExecutionStatus(0);
        return plan;
    }

    private void validateParams(Relationship ship) {
        if (ship.getBirthdayMonth() == null
                || ship.getBirthdayDay() == null) {
            throw new RuntimeException("生日月份和日期不能为空");
        }
        if (StrUtil.isBlank(ship.getMyEmail())){
            throw new RuntimeException("我的邮箱不能为空");
        }
    }

    private LocalDate calculateBirthday(Relationship ship) {
        // 0-公历；1-农历
        Integer calendarType = ship.getCalendarType();
        // 暂时只支持公历
        if (calendarType == 1){
            throw new RuntimeException("暂不支持农历生日");
        }
        return calculateGregorianBirthday(ship);
    }

    /**
     * 计算公历的下一个生日
     *
     * @param relationship
     * @return
     */
    private LocalDate calculateGregorianBirthday(Relationship relationship) {
        LocalDate now = LocalDate.now();
        int month = relationship.getBirthdayMonth();
        int day = relationship.getBirthdayDay();

        // 处理闰日情况
        boolean isLeapDay = month == 2 && day == 29;
        // 如果今年没有2月29日，则改为2月28日
        if (isLeapDay && !now.isLeapYear()) {
            day = 28;  // 非闰年使用2月28日
        }

        LocalDate birthdayThisYear = buildValidDate(now.getYear(), month, day);
        LocalDate nextBirthday = birthdayThisYear.isBefore(now)
                ? calculateNextGregorianBirthday(birthdayThisYear, isLeapDay)
                : birthdayThisYear;

        return nextBirthday;
    }

    private LocalDate buildValidDate(int year, int month, int day) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new RuntimeException("无效的日期组合: 年=" + year
                    + " 月=" + month + " 日=" + day);
        }
    }
    /**
     * 计算明年的生日
     *
     * @param birthday
     * @param isLeapDay
     * @return
     */
    private LocalDate calculateNextGregorianBirthday(LocalDate birthday, boolean isLeapDay) {
        LocalDate nextBirthday = birthday.plusYears(1);

        if (isLeapDay) {
            return nextBirthday.isLeapYear() ? nextBirthday.withDayOfMonth(29) : nextBirthday.withDayOfMonth(28);
        }
        return nextBirthday;
    }

    @Override
    public PageInfo<ReminderPlan> getPageList(Integer page, Integer size, Integer relationshipId) {
        PageHelper.startPage(page, size);
        List<ReminderPlan> list = mapper.selectAll(relationshipId);
        return new PageInfo<>(list);
    }

    @Override
    public ReminderPlan getById(Integer id) {
        return mapper.selectById(id);
    }

    @Override
    public void create(ReminderPlan reminderPlan) {
        mapper.insert(reminderPlan);
    }

    @Override
    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }
}
