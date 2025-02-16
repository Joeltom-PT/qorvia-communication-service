package com.qorvia.communicationservice.model;

import lombok.Data;

@Data
public class EventInfo {
    private String name;
    private Long organizerId;
    private String imageUrl;
}
