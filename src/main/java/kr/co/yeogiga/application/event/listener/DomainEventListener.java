package kr.co.yeogiga.application.event.listener;

import kr.co.yeogiga.domain.event.DomainEvent;

public interface DomainEventListener {
    void handleEvent(DomainEvent event);
}
