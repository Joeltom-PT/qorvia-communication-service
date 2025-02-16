package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.dto.ScheduleEventDTO;
import com.qorvia.communicationservice.model.Schedules;
import com.qorvia.communicationservice.repository.SchedulesRepository;
import com.qorvia.communicationservice.utils.AccessCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    private final SchedulesRepository schedulesRepository;
    private final AccessCodeUtil accessCodeUtil;
    private final CronJobManagerService cronJobManagerService;

    @Override
    public void schedule(ScheduleEventDTO eventDTO) {
        String accessCode = accessCodeUtil.generateAccessCodePublic(eventDTO.getEventId(),
                eventDTO.getStartDateAndTime(),
                eventDTO.getOrganizerId());

        log.info("Access code generated: {}", accessCode);

        log.info("Event starting time : {} and the ending time : {}",  eventDTO.getStartDateAndTime(), eventDTO.getEndDateAndTime());

        LocalDateTime startDateTime = LocalDateTime.parse(eventDTO.getStartDateAndTime());
        LocalDateTime endDateTime = LocalDateTime.parse(eventDTO.getEndDateAndTime());

        Optional<Schedules> optionalSchedules = schedulesRepository.findByEventId(eventDTO.getEventId());

        if (optionalSchedules.isPresent()) {
            Schedules existingSchedule = optionalSchedules.get();
            cronJobManagerService.deleteCron(existingSchedule.getAccessCode());
            schedulesRepository.delete(existingSchedule);
            log.info("Existing schedule and cron job deleted for eventId: {}", eventDTO.getEventId());
        }

        Schedules schedule = Schedules.builder()
                .id(UUID.randomUUID())
                .eventId(eventDTO.getEventId())
                .accessCode(accessCode)
                .startDateAndTime(startDateTime)
                .endDateAndTime(endDateTime)
                .build();

        schedulesRepository.save(schedule);

        LocalDateTime cronTriggerTime = startDateTime.minusMinutes(5);
        cronJobManagerService.scheduleCron(accessCode, cronTriggerTime);

        log.info("New schedule and cron job created for eventId: {}", eventDTO.getEventId());
    }


}
