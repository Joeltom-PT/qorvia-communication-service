package com.qorvia.communicationservice.service;

import com.qorvia.communicationservice.dto.RoomAccessDTO;
import com.qorvia.communicationservice.dto.SteamingKeyAndEventInfoDTO;
import com.qorvia.communicationservice.dto.StreamingAccessDTO;
import com.qorvia.communicationservice.model.Event;
import com.qorvia.communicationservice.model.EventInfo;
import com.qorvia.communicationservice.model.RoomAccess;
import com.qorvia.communicationservice.model.Schedules;
import com.qorvia.communicationservice.repository.EventRepository;
import com.qorvia.communicationservice.repository.RoomAccessRepository;
import com.qorvia.communicationservice.repository.SchedulesRepository;
import com.qorvia.communicationservice.socket.RoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomAccessRepository roomAccessRepository;
    private final SchedulesRepository schedulesRepository;
    private final EventRepository eventRepository;

    @Value("${streaming.url}")
    private String streamingUrl;


    @Override
    public void allowAccess(RoomAccessDTO roomAccessDTO) {
        Long userId = roomAccessDTO.getUserId();
        String eventId = roomAccessDTO.getEventId();
        String userEmail = roomAccessDTO.getUserEmail();

        Optional<RoomAccess> roomAccess = roomAccessRepository.findByUserIdAndEventId(userId, eventId);

        if (roomAccess.isPresent()) {
            log.info("Access already granted.");
        }

        RoomAccess newRoomAccess = new RoomAccess();
        newRoomAccess.setId(UUID.randomUUID());
        newRoomAccess.setUserId(userId);
        newRoomAccess.setEventId(eventId);
        newRoomAccess.setUserEmail(userEmail);

        roomAccessRepository.save(newRoomAccess);
        log.info("Access granted successfully.");
    }

    @Override
    public ResponseEntity<StreamingAccessDTO> getStreamKey(Long organizerId, String eventId) {
        Optional<Event> optionalEvent = eventRepository.findByEventId(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        Event event = optionalEvent.get();

        EventInfo eventInfo = event.getEventInfo();

        if (!Objects.equals(eventInfo.getOrganizerId(), organizerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        StreamingAccessDTO accessDTO = new StreamingAccessDTO();
        accessDTO.setStreamingKey(event.getSteamingId());
        accessDTO.setStreamingUrl(streamingUrl);
        return ResponseEntity.ok(accessDTO);
    }

    @Override
    public ResponseEntity<?> checkAndVerifyOrganizer(String eventId, long organizerId, String organizerEmail) {
        Optional<Event> eventOptional = eventRepository.findByEventId(eventId);

        if (eventOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found.");
        }

        Event event = eventOptional.get();
        EventInfo eventInfo = event.getEventInfo();

        if (eventInfo.getOrganizerId() != organizerId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Organizer ID does not match.");
        }

        Optional<RoomAccess> roomAccessOptional = roomAccessRepository.findByUserIdAndEventId(organizerId, eventId);
        if (roomAccessOptional.isPresent()) {
            return ResponseEntity.ok("Access already granted.");
        }

        RoomAccess newRoomAccess = new RoomAccess();
        newRoomAccess.setId(UUID.randomUUID());
        newRoomAccess.setUserId(organizerId);
        newRoomAccess.setEventId(eventId);
        newRoomAccess.setUserEmail(organizerEmail);

        roomAccessRepository.save(newRoomAccess);

        return ResponseEntity.status(HttpStatus.CREATED).body("Access granted successfully.");
    }

    @Override
    public ResponseEntity<?> getSteamingKeyAndEventInfo(Long userId, String eventId) {
        Optional<RoomAccess> roomAccess = roomAccessRepository.findByUserIdAndEventId(userId, eventId);
        if (roomAccess.isPresent()) {
            Optional<Event> optionalEvent = eventRepository.findByEventId(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                SteamingKeyAndEventInfoDTO steamingKeyAndEventInfoDTO = SteamingKeyAndEventInfoDTO.builder()
                        .eventId(eventId)
                        .steamingKey(event.getSteamingId())
                        .imageUrl(event.getEventInfo().getImageUrl())
                        .eventName(event.getEventInfo().getName())
                        .build();
                return ResponseEntity.ok(steamingKeyAndEventInfoDTO);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not found");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have access to the stream for this event");
    }



}
