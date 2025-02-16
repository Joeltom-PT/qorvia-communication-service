package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.job.AccessCodeJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronJobManagerService {

    private final Scheduler scheduler;

    public void scheduleCron(String accessCode, LocalDateTime dateTime) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(AccessCodeJob.class)
                    .withIdentity("AccessCodeJob-" + accessCode, "AccessCodeGroup")
                    .usingJobData("accessCode", accessCode)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("AccessCodeTrigger-" + accessCode, "AccessCodeGroup")
                    .startAt(java.sql.Timestamp.valueOf(dateTime))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Scheduled Quartz job for access code: {} at {}", accessCode, dateTime);
        } catch (SchedulerException e) {
            log.error("Error scheduling job for access code: {}", accessCode, e);
        }
    }

    public void deleteCron(String accessCode) {
        try {
            JobKey jobKey = JobKey.jobKey("AccessCodeJob-" + accessCode, "AccessCodeGroup");
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("Deleted Quartz job for access code: {}", accessCode);
            } else {
                log.warn("No Quartz job found for access code: {}", accessCode);
            }
        } catch (SchedulerException e) {
            log.error("Error deleting job for access code: {}", accessCode, e);
        }
    }

}
