package com.qorvia.communicationservice.repository;

import com.qorvia.communicationservice.model.Schedules;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchedulesRepository extends MongoRepository<Schedules, UUID> {
    Optional<Schedules> findByEventId(String eventId);

    Schedules findByAccessCode(String accessCode);
}
