package com.Birthday_reminder.task;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.Birthday_reminder.domain.ReminderRecord;
import com.Birthday_reminder.service.ReminderRecordService;
import com.Birthday_reminder.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class MailJob {

    @Autowired
    private MailUtil mailUtil;
    
    @Autowired
    private ReminderRecordService reminderRecordService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void sendReminderEmails() {
        try {
            log.info("开始执行邮件发送任务");
            
            // 查询待发送的提醒记录
            List<ReminderRecord> pendingRecords = reminderRecordService.getPendingRecords();
            
            if (CollUtil.isEmpty(pendingRecords)) {
                log.info("没有待发送的提醒记录");
                return;
            }
            
            log.info("找到 {} 条待发送的提醒记录", pendingRecords.size());
            
            // 读取邮件模板
            String htmlTemplate = ResourceUtil.readUtf8Str("Mail-html/notice.html");
            
            // 逐条发送邮件
            for (ReminderRecord record : pendingRecords) {
                try {
                    sendSingleReminder(record, htmlTemplate);
                } catch (Exception e) {
                    log.error("发送提醒邮件失败, 记录ID: {}, 错误: {}", record.getId(), e.getMessage(), e);
                }
            }
            
            log.info("==== 邮件发送任务执行完毕 ====");
        } catch (Exception e) {
            log.error("邮件发送任务异常: {}", e.getMessage(), e);
        }
    }

    private void sendSingleReminder(ReminderRecord record, String htmlTemplate) {
        if (record.getReceiver() == null || record.getReceiver().isEmpty()) {
            log.warn("记录ID {} 没有接收者邮箱，跳过", record.getId());
            return;
        }
        
        // 根据 reminderType 决定发送哪种邮件
        // 0-提醒用户；1-发送祝贺
        if (record.getReminderType() == 0) {
            sendReminderEmail(record, htmlTemplate);
        } else if (record.getReminderType() == 1) {
            sendCongratulateEmail(record);
        }
    }
    

    private void sendReminderEmail(ReminderRecord record, String htmlTemplate) {
        // 计算距离生日的天数
        Integer daysUntilBirthday = calculateDaysUntilBirthday(record.getBirthdayDate());
        
        // 准备邮件模板参数
        Map<String, Object> params = new HashMap<>(3);
        params.put("relationshipName", record.getRelationshipName() != null ? record.getRelationshipName() : "亲友");
        params.put("birthday", record.getBirthdayDate() != null ? record.getBirthdayDate() : "未知");
        params.put("days", daysUntilBirthday != null ? daysUntilBirthday : 0);
        
        // 填充模板
        String htmlContent = StrUtil.format(htmlTemplate, params);
        
        // 构建邮件主题
        String subject = String.format("🎂 生日提醒：%s 的生日就要到了！", 
            record.getRelationshipName() != null ? record.getRelationshipName() : "亲友");
        
        // 发送邮件
        mailUtil.sendHtmlMail(subject, htmlContent, record.getReceiver());
        
        log.info("成功发送提醒邮件到: {}, 亲友: {}, 距离生日: {}天", 
            record.getReceiver(), record.getRelationshipName(), daysUntilBirthday);
    }
    

    private void sendCongratulateEmail(ReminderRecord record) {
        try {
            // 读取祝贺邮件模板
            String congratulateTemplate = ResourceUtil.readUtf8Str("Mail-html/congratulate.html");
            
            // 准备模板参数
            Map<String, Object> params = new HashMap<>(3);
            params.put("relationshipName", record.getRelationshipName() != null ? record.getRelationshipName() : "朋友");
            params.put("greeting", record.getGreeting() != null && !record.getGreeting().isEmpty() ? record.getGreeting() : "亲爱的");
            params.put("selfCall", record.getSelfCall() != null && !record.getSelfCall().isEmpty() ? record.getSelfCall() : "你的朋友");
            
            // 填充模板
            String htmlContent = StrUtil.format(congratulateTemplate, params);
            
            // 构建邮件主题
            String subject = String.format("🎉 生日快乐！%s", 
                record.getRelationshipName() != null ? record.getRelationshipName() : "");
            
            // 发送邮件
            mailUtil.sendHtmlMail(subject, htmlContent, record.getReceiver());
            
            log.info("成功发送祝贺邮件到: {}, 亲友: {}", 
                record.getReceiver(), record.getRelationshipName());
        } catch (Exception e) {
            log.error("发送祝贺邮件失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    

    private Integer calculateDaysUntilBirthday(String birthdayStr) {
        if (birthdayStr == null || birthdayStr.isEmpty()) {
            return 0;
        }
        
        try {
            LocalDate birthday = LocalDate.parse(birthdayStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();
            
            // 获取今年的生日
            LocalDate thisYearBirthday = birthday.withYear(today.getYear());
            
            // 如果今年的生日已经过了，就计算明年的
            if (thisYearBirthday.isBefore(today)) {
                thisYearBirthday = thisYearBirthday.plusYears(1);
            }
            
            return (int) ChronoUnit.DAYS.between(today, thisYearBirthday);
        } catch (Exception e) {
            log.error("计算生日天数失败: {}", e.getMessage());
            return 0;
        }
    }
}
