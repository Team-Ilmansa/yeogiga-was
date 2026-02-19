package kr.co.yeogiga.application.event.cleaner.strategy;

import java.util.List;

public interface EventCleanupStrategy {
    String getName();
    
    String getCronExpression();
    
    long getChunkSize();
    
    List<Long> getIdsToDelete();
    
    String getSuccessMessage();
    
    String getFailMessage();
}
