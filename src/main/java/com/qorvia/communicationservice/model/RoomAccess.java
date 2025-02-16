package com.qorvia.communicationservice.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;


@Data
@Document(collection = "room_access")
public class RoomAccess {
    @Id
    private UUID id;

    @Field("event_id")
    private String eventId;

    @Field("user_id")
    private Long userId;

    @Field("user_email")
    private String userEmail;
}