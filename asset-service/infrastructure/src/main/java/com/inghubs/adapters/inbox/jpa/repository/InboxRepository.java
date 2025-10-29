package com.inghubs.adapters.inbox.jpa.repository;

import com.inghubs.adapters.inbox.jpa.entity.InboxEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxEntity, UUID> {

}
