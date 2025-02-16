package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.dto.ScheduleEventDTO;
import org.springframework.http.ResponseEntity;

public interface EventService {
    void scheduleEvent(ScheduleEventDTO eventDTO);
}
