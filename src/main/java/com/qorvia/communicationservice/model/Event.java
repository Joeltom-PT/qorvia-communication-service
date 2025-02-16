package com.qorvia.communicationservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document(collection = "online_events")
public class Event {
    @Id
    private UUID id;

    @Field("event_id")
    @Indexed
    private String eventId;

    @Field("steaming_id")
    private String steamingId;

    private Boolean isStreaming;

    private EventInfo eventInfo;
}
