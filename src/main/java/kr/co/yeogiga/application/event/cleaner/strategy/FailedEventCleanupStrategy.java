package kr.co.yeogiga.application.event.cleaner.strategy;

import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FailedEventCleanupStrategy implements EventCleanupStrategy {
    private final EventOutboxService eventOutboxService;
    private final long CHUNK_SIZE = 300L;
    
    @Override
    public String getName() {
        return "FailedEventCleanupStrategy";
    }
    
    @Override
    public String getCronExpression() {
        return "0 30 0 * * *";
    }
    
    @Override
    public long getChunkSize() {
        return CHUNK_SIZE;
    }
    
    @Override
    public List<Long> getIdsToDelete() {
        return eventOutboxService.findOldFailedEventIds(3, LocalDateTime.now().minusDays(1), CHUNK_SIZE);
    }
    
    @Override
    public String getSuccessMessage() {
        return "Success to delete FAILED event.";
    }
    
    @Override
    public String getFailMessage() {
        return "Failed to delete FAILED event.";
    }
}
