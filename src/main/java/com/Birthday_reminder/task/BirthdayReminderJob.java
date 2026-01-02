package com.Birthday_reminder.task;
import cn.hutool.core.collection.CollUtil;
import com.Birthday_reminder.domain.ReminderPlan;
import com.Birthday_reminder.service.RelationPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class BirthdayReminderJob {

    @Autowired
    private RelationPlanService planService;

    @Scheduled(cron = "0 0 9,12,20 * * ?")//

    @Transactional
    public void reminder() {
        // 扫描当天未执行的计划
        planService.executePlan(LocalDate.now());

    }
}
