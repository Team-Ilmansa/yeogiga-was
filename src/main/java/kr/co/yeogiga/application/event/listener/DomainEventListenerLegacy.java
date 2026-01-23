package kr.co.yeogiga.application.event.listener;

import kr.co.yeogiga.domain.event.DomainEvent;

public abstract class DomainEventListenerLegacy<T extends DomainEvent> {
    public abstract void handleEvent(T event);
}
