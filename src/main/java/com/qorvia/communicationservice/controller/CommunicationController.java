package com.qorvia.communicationservice.controller;

import com.qorvia.communicationservice.dto.RoomAccessDTO;
import com.qorvia.communicationservice.dto.ScheduleEventDTO;
import com.qorvia.communicationservice.dto.StreamingAccessDTO;
import com.qorvia.communicationservice.security.RequireRole;
import com.qorvia.communicationservice.security.Roles;
import com.qorvia.communicationservice.service.EventService;
import com.qorvia.communicationservice.service.RoomService;
import com.qorvia.communicationservice.service.jwt.JwtService;
import com.qorvia.communicationservice.socket.RoomManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final EventService eventService;
    private final RoomService roomService;
    private final RoomManager roomManager;
    private final JwtService jwtService;


    @PostMapping("/organizer-asking-access/{id}")
    @RequireRole(role = Roles.ORGANIZER)
    public ResponseEntity<?> organizerAskingAccessToRoom(@PathVariable("id") String eventId, HttpServletRequest servletRequest){
        String token = jwtService.getJWTFromRequest(servletRequest);
        long organizerId = jwtService.getUserIdFromToken(token);
        String organizerEmail = jwtService.getEmailFromToken(token);
        return roomService.checkAndVerifyOrganizer(eventId, organizerId, organizerEmail);
    }

    @GetMapping("/validate/{eventId}")
    public ResponseEntity<Boolean> validateAccess(HttpServletRequest servletRequest, @PathVariable String eventId) {
        Long userId = jwtService.getUserIdFormRequest(servletRequest);
        boolean hasAccess = roomManager.hasAccess(eventId, userId);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping("/get-streaming-key/organizer/{eventId}")
    @RequireRole(role = Roles.ORGANIZER)
    public ResponseEntity<StreamingAccessDTO> getStreamingKeyForOrganizer(@PathVariable("eventId") String eventId, HttpServletRequest servletRequest) {
        Long organizerId = jwtService.getUserIdFormRequest(servletRequest);
        return roomService.getStreamKey(organizerId, eventId);
    }

    @GetMapping("/get-streaming-key/user/{eventId}")
    @RequireRole(role = Roles.USER)
    public ResponseEntity<?> getSteamingKeyAndEventInfo(@PathVariable("eventId") String eventId, HttpServletRequest servletRequest) {
        Long userId = jwtService.getUserIdFormRequest(servletRequest);
        return roomService.getSteamingKeyAndEventInfo(userId, eventId);
    }


}
