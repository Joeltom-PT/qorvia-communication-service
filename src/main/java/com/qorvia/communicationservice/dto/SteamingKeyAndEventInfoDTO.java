package com.qorvia.communicationservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SteamingKeyAndEventInfoDTO {
    private String eventId;
    private String steamingKey;
    private String imageUrl;
    private String eventName;
}
