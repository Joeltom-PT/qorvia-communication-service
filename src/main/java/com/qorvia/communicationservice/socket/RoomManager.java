package com.qorvia.communicationservice.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.qorvia.communicationservice.dto.ChatMessageDTO;
import com.qorvia.communicationservice.model.Event;
import com.qorvia.communicationservice.model.Schedules;
import com.qorvia.communicationservice.repository.EventRepository;
import com.qorvia.communicationservice.repository.RoomAccessRepository;
import com.qorvia.communicationservice.repository.SchedulesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomManager {

    private final SocketIOServer socketIOServer;
    private final SchedulesRepository schedulesRepository;
    private final EventRepository eventRepository;
    private final RoomAccessRepository roomAccessRepository;

    //////////////////////////////////////////////////////

    public void findAction(String accessCode) {
        Schedules schedule = schedulesRepository.findByAccessCode(accessCode);
        Event event = eventRepository.findByEventId(schedule.getEventId()).get();

        if (event.getIsStreaming()) {
            stopRoom(event.getEventId());
        } else {
            startRoom(event.getEventId());
        }
    }

    public boolean hasAccess(String eventId, Long userId) {
        log.info("checking user access to the event by event id : {} and user Id : {}", eventId, userId);
        return roomAccessRepository.existsByEventIdAndUserId(eventId, userId);
    }

    public boolean hasSocketAccess(String eventId, String userEmail){

        log.info("Checking assess for user : {} to event : {}", userEmail, eventId);
        return roomAccessRepository.existsByEventIdAndUserEmail(eventId, userEmail);
    }

    ////////////////////////////////////////////////////

    public void startRoom(String eventId) {
        String roomId = "room_" + eventId;
        log.info("Room {} started for event {}", roomId, eventId);

        socketIOServer.getRoomOperations(roomId).sendEvent("room_status", "Room started for event " + eventId);
    }

    public void stopRoom(String eventId) {
        String roomId = "room_" + eventId;

        log.info("Stopping room {}", roomId);
        socketIOServer.getRoomOperations(roomId).sendEvent("room_status", "Room ended for event " + eventId);

        socketIOServer.getRoomOperations(roomId).getClients().forEach(client -> {
            client.disconnect();
        });

        log.info("Room {} has been stopped", roomId);
    }

    public void broadcastMessageToRoom(String eventId, ChatMessageDTO chatMessageDTO) {
        String roomId = eventId;

        log.info("Broadcasting message to room {}: User {} - {}", roomId, chatMessageDTO.getUserId(), chatMessageDTO.getMessage());
        socketIOServer.getRoomOperations(roomId).sendEvent("chat_message", chatMessageDTO);
    }

    public void addUserToRoom(SocketIOClient client, String eventId, String userId) {
        String roomId = "room_" + eventId;

        if (!hasSocketAccess(eventId, userId)) {
            log.warn("User {} does not have access to join room {}", userId, roomId);
            client.sendEvent("room_status", "Access denied for event " + eventId);
            return;
        }

        client.joinRoom(roomId);
        client.set("roomId", roomId);
        client.set("userId", userId);
        log.info("User {} added to room {}", userId, roomId);

        client.sendEvent("room_status", "You have joined the room for event " + eventId);
    }

    public void removeUserFromRoom(SocketIOClient client, String eventId) {
        String roomId = "room_" + eventId;

        client.leaveRoom(roomId);
        log.info("User {} removed from room {}", client.getSessionId(), roomId);

        client.sendEvent("room_status", "You have left the room for event " + eventId);
    }
}
