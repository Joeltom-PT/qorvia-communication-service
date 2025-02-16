package com.qorvia.communicationservice.dto;

import lombok.Data;

@Data
public class RoomAccessDTO {
    private String eventId;
    private Long userId;
    private String userEmail;
}
