package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.dto.RoomAccessDTO;
import com.qorvia.communicationservice.dto.StreamingAccessDTO;
import org.springframework.http.ResponseEntity;

public interface RoomService {
    void allowAccess(RoomAccessDTO roomAccessDTO);

    ResponseEntity<StreamingAccessDTO> getStreamKey(Long organizerId, String eventId);

    ResponseEntity<?> checkAndVerifyOrganizer(String eventId, long organizerId, String organizerEmail);

    ResponseEntity<?> getSteamingKeyAndEventInfo(Long userId, String eventId);
}
