package com.qorvia.communicationservice.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.qorvia.communicationservice.dto.ChatMessageDTO;
import com.qorvia.communicationservice.dto.JoinRoomPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventHandler {

    private final RoomManager roomManager;

    public void registerListeners(SocketIOServer server) {
        server.addEventListener("join_room", JoinRoomPayload.class, (client, data, ackRequest) -> {
            String userId = data.getUserId();

            if (userId == null) {
                log.warn("UserId not found for client {}", client.getSessionId());
                client.sendEvent("room_status", "UserId not found. Please authenticate.");
                return;
            }

            client.set("roomId", data.getEventId());
            roomManager.addUserToRoom(client, data.getEventId(), userId);
        });

        server.addEventListener("chat_message", ChatMessageDTO.class, (client, chatMessageDTO, ackRequest) -> {
            String roomId = (String) client.get("roomId");

            if (roomId == null) {
                log.warn("RoomId not found for client {}", client.getSessionId());
                client.sendEvent("room_status", "Room not found. Please join a room first.");
                return;
            }

            roomManager.broadcastMessageToRoom(roomId, chatMessageDTO);
        });
    }
}
