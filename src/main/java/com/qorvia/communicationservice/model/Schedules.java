package com.qorvia.communicationservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document(collection = "event_schedules")
public class Schedules {
    @Id
    private UUID id;

    @Field("event_id")
    private String eventId;

    @Field("access_code")
    private String accessCode;

    @Field("start_date_and_time")
    private LocalDateTime startDateAndTime;

    @Field("end_date_and_time")
    private LocalDateTime endDateAndTime;
}