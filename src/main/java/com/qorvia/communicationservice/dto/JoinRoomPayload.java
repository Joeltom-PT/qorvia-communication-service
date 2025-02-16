package com.qorvia.communicationservice.dto;

import lombok.Data;

@Data
public class JoinRoomPayload {
    private String eventId;
    private String userId;

}
