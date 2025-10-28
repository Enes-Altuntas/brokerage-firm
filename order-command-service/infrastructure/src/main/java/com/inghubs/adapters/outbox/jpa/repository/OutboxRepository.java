package com.inghubs.adapters.outbox.jpa.repository;

import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

}
