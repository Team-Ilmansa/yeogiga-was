package kr.co.yeogiga.domain.outbox.repository;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, Long> {

}
