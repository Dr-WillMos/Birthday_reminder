package com.Birthday_reminder.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RelationshipPageVO implements Serializable {
    private Integer id;

    /**
     * 亲友姓名
     */
    private String name;

    /**
     * 0代表公历，1代表农历
     */
    private Integer calendarType;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 生日月份
     */
    private Integer birthdayMonth;

    /**
     * 生日-天
     */
    private Integer birthdayDay;

    /**
     * 标签
     */
    private String tag;

    /**
     * 是否启用提醒
     */
    private Integer remindEnabled;

    /**
     * 是否替我祝贺
     */
    private Integer congratulateEnabled;

    /**
     * 亲友邮箱
     */
    private String relationshipEmail;

    /**
     * 招呼语
     */
    private String greeting;

    /**
     * 自称
     */
    private String selfCall;

    /**
     * 备注
     */
    private String notes;

    /**
     * 提前通知的天数
     */
    private Object daysBefore;
}
