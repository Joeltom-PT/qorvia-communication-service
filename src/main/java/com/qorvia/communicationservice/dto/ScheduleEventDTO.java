package com.qorvia.communicationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleEventDTO {
    private String eventId;
    private String name;
    private Long organizerId;
    private String imageUrl;
    private String startDateAndTime;
    private String endDateAndTime;
}
