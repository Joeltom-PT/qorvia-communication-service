package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.dto.ScheduleEventDTO;

public interface SchedulerService{
    void schedule(ScheduleEventDTO eventDTO);
}
