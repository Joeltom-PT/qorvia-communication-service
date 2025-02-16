package com.qorvia.communicationservice.job;

import com.qorvia.communicationservice.service.RoomService;
import com.qorvia.communicationservice.socket.RoomManager;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccessCodeJob implements Job {

    private final RoomManager roomManager;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String accessCode = context.getJobDetail().getJobDataMap().getString("accessCode");
        log.info("Executing job for access code: {}", accessCode);
         // here I need to perfrom the start event

        roomManager.findAction(accessCode);
    }
}
