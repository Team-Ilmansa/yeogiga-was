package kr.co.yeogiga.application.event.cleaner.strategy;

import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingEventCleanupStrategy implements EventCleanupStrategy {
    private final EventOutboxService eventOutboxService;
    private final long CHUNK_SIZE = 300L;
    
    @Override
    public String getName() {
        return "WaitingEventCleanupStrategy";
    }
    
    @Override
    public String getCronExpression() {
        return "0 0 0 * * *";
    }
    
    @Override
    public long getChunkSize() {
        return CHUNK_SIZE;
    }
    
    @Override
    public List<Long> getIdsToDelete() {
        return eventOutboxService.findOldWaitingEventIds(LocalDateTime.now().minusDays(1), CHUNK_SIZE);
    }
    
    @Override
    public String getSuccessMessage() {
        return "Success to delete WAITING event.";
    }
    
    @Override
    public String getFailMessage() {
        return "Failed to delete WAITING event.";
    }
}
