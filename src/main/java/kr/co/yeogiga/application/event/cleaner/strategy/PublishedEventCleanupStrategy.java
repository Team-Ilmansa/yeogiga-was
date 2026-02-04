package kr.co.yeogiga.application.event.cleaner.strategy;

import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PublishedEventCleanupStrategy implements EventCleanupStrategy {
    private final EventOutboxService eventOutboxService;
    private final long CHUNK_SIZE = 1_000L;
    
    @Override
    public String getName() {
        return "PublishedEventCleanupStrategy";
    }
    
    @Override
    public String getCronExpression() {
        return "0 0,30 * * * *";
    }
    
    @Override
    public long getChunkSize() {
        return CHUNK_SIZE;
    }
    
    @Override
    public List<Long> getIdsToDelete() {
        return eventOutboxService.findOldPublishedEventIds(LocalDateTime.now().minusMinutes(30), CHUNK_SIZE);
    }
    
    @Override
    public String getSuccessMessage() {
        return "Success to delete PUBLISHED event.";
    }
    
    @Override
    public String getFailMessage() {
        return "Failed to delete PUBLISHED event.";
    }
}
