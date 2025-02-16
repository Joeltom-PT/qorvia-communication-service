package com.qorvia.communicationservice.repository;

import com.qorvia.communicationservice.model.RoomAccess;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomAccessRepository extends MongoRepository<RoomAccess, UUID> {
    Optional<RoomAccess> findByUserIdAndEventId(Long userId, String eventId);

    boolean existsByEventIdAndUserId(String eventId, Long userId);

    boolean existsByEventIdAndUserEmail(String eventId, String userEmail);
}
