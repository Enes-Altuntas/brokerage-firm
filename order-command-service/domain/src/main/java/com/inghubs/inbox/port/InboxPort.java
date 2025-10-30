package com.inghubs.inbox.port;

import com.inghubs.common.model.Event;
import com.inghubs.inbox.model.Inbox;
import java.util.UUID;

public interface InboxPort {

  Inbox retrieveInboxById(UUID id);

  void createInboxEntity(Event event);

}
