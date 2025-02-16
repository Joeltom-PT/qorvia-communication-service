package com.qorvia.communicationservice.repository;

import com.qorvia.communicationservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends MongoRepository<Event, UUID> {
    Optional<Event> findByEventId(String eventId);
}
