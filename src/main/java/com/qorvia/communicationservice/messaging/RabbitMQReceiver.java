package com.qorvia.communicationservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.qorvia.communicationservice.dto.RoomAccessDTO;
import com.qorvia.communicationservice.dto.ScheduleEventDTO;
import com.qorvia.communicationservice.dto.message.RoomAccessMessage;
import com.qorvia.communicationservice.dto.message.ScheduleEventMessage;
import com.qorvia.communicationservice.service.EventService;
import com.qorvia.communicationservice.service.RoomService;
import com.qorvia.communicationservice.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQReceiver {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RoomService roomService;
    private final EventService eventService;

    @RabbitListener(queues = {AppConstants.COMMUNICATION_SERVICE_ASYNC_QUEUE})
    public void receiveMessage(Message amqpMessage) {
        try {
            byte[] messageBytes = amqpMessage.getBody();
            log.info("I am getting the message bytes as : ========================================== : {}", new String(messageBytes, StandardCharsets.UTF_8));
            MessageProperties amqpProps = amqpMessage.getMessageProperties();
            String correlationId = amqpProps.getCorrelationId();
            if (correlationId != null) {
                log.info("Received RPC message with correlation ID: {}", correlationId);
            }

            Map<String, Object> messageMap = objectMapper.readValue(messageBytes, Map.class);
            String type = (String) messageMap.get("type");

            switch (type) {
                case "room-access-message":
                    RoomAccessMessage roomAccessMessage = objectMapper.convertValue(messageMap, RoomAccessMessage.class);
                    handleRoomAccessMessage(roomAccessMessage);
                    break;
                case "schedule-event-message":
                    ScheduleEventMessage scheduleEventMessage = objectMapper.convertValue(messageMap, ScheduleEventMessage.class);
                    handleScheduleEventMessage(scheduleEventMessage);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown message type: " + type);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize message", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process message", e);
        }
    }

    private void handleRoomAccessMessage(RoomAccessMessage message) {
        RoomAccessDTO roomAccessDTO = new RoomAccessDTO();
        roomAccessDTO.setEventId(message.getEventId());
        roomAccessDTO.setUserId(message.getUserId());
        roomAccessDTO.setUserEmail(message.getUserEmail());

        roomService.allowAccess(roomAccessDTO);
    }


    private void handleScheduleEventMessage(ScheduleEventMessage message) {

        ScheduleEventDTO scheduleEventDTO = new ScheduleEventDTO();
        scheduleEventDTO.setEventId(message.getEventId());
        scheduleEventDTO.setName(message.getName());
        scheduleEventDTO.setOrganizerId(message.getOrganizerId());
        scheduleEventDTO.setImageUrl(message.getImageUrl());
        scheduleEventDTO.setEndDateAndTime(message.getEndDateAndTime());
        scheduleEventDTO.setStartDateAndTime(message.getStartDateAndTime());

        eventService.scheduleEvent(scheduleEventDTO);
    }


    private void sendRpcResponse(MessageProperties amqpProps, Object response) throws JsonProcessingException {
        byte[] responseBytes = objectMapper.writeValueAsBytes(response);
        MessageProperties responseProperties = new MessageProperties();
        responseProperties.setCorrelationId(amqpProps.getCorrelationId());
        responseProperties.setContentType("application/octet-stream");

        Message responseMessage = new Message(responseBytes, responseProperties);
        rabbitTemplate.send(amqpProps.getReplyTo(), responseMessage);
    }
}
