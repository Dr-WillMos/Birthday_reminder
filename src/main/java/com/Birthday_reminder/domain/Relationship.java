package com.Birthday_reminder.domain;
//domain层是实体类，记录了三张表对应的实体类
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName relationship
 */
@Data//生成实体类的过程是由MybatisX插件生成的
public class Relationship implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 亲友姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String myEmail;
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

    private List<Integer> daysBefore;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Relationship other = (Relationship) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCalendarType() == null ? other.getCalendarType() == null : this.getCalendarType().equals(other.getCalendarType()))
            && (this.getBirthday() == null ? other.getBirthday() == null : this.getBirthday().equals(other.getBirthday()))
            && (this.getBirthdayMonth() == null ? other.getBirthdayMonth() == null : this.getBirthdayMonth().equals(other.getBirthdayMonth()))
            && (this.getBirthdayDay() == null ? other.getBirthdayDay() == null : this.getBirthdayDay().equals(other.getBirthdayDay()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getRemindEnabled() == null ? other.getRemindEnabled() == null : this.getRemindEnabled().equals(other.getRemindEnabled()))
            && (this.getCongratulateEnabled() == null ? other.getCongratulateEnabled() == null : this.getCongratulateEnabled().equals(other.getCongratulateEnabled()))
            && (this.getRelationshipEmail() == null ? other.getRelationshipEmail() == null : this.getRelationshipEmail().equals(other.getRelationshipEmail()))
            && (this.getGreeting() == null ? other.getGreeting() == null : this.getGreeting().equals(other.getGreeting()))
            && (this.getSelfCall() == null ? other.getSelfCall() == null : this.getSelfCall().equals(other.getSelfCall()))
            && (this.getNotes() == null ? other.getNotes() == null : this.getNotes().equals(other.getNotes()))
            && (this.getDaysBefore() == null ? other.getDaysBefore() == null : this.getDaysBefore().equals(other.getDaysBefore()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCalendarType() == null) ? 0 : getCalendarType().hashCode());
        result = prime * result + ((getBirthday() == null) ? 0 : getBirthday().hashCode());
        result = prime * result + ((getBirthdayMonth() == null) ? 0 : getBirthdayMonth().hashCode());
        result = prime * result + ((getBirthdayDay() == null) ? 0 : getBirthdayDay().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getRemindEnabled() == null) ? 0 : getRemindEnabled().hashCode());
        result = prime * result + ((getCongratulateEnabled() == null) ? 0 : getCongratulateEnabled().hashCode());
        result = prime * result + ((getRelationshipEmail() == null) ? 0 : getRelationshipEmail().hashCode());
        result = prime * result + ((getGreeting() == null) ? 0 : getGreeting().hashCode());
        result = prime * result + ((getSelfCall() == null) ? 0 : getSelfCall().hashCode());
        result = prime * result + ((getNotes() == null) ? 0 : getNotes().hashCode());
        result = prime * result + ((getDaysBefore() == null) ? 0 : getDaysBefore().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", calendarType=").append(calendarType);
        sb.append(", birthday=").append(birthday);
        sb.append(", birthdayMonth=").append(birthdayMonth);
        sb.append(", birthdayDay=").append(birthdayDay);
        sb.append(", tag=").append(tag);
        sb.append(", remindEnabled=").append(remindEnabled);
        sb.append(", congratulateEnabled=").append(congratulateEnabled);
        sb.append(", relationshipEmail=").append(relationshipEmail);
        sb.append(", greeting=").append(greeting);
        sb.append(", selfCall=").append(selfCall);
        sb.append(", notes=").append(notes);
        sb.append(", daysBefore=").append(daysBefore);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}