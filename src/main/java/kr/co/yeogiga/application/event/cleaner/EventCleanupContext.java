package kr.co.yeogiga.application.event.cleaner;

import kr.co.yeogiga.application.event.cleaner.strategy.EventCleanupStrategy;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCleanupContext {
    private final EventOutboxService eventOutboxService;
    
    /**
     * 이벤트 삭제 로직을 안전하게 수행하기 위한 래핑 메서드
     *
     * <p> 삭제 진행 시 발생 예외를 감싸 후처리를 가능하도록 한다.
     *
     * @param strategy
     */
    public void executeSafe(EventCleanupStrategy strategy) {
        try {
            execute(strategy);
        } catch (Exception e) {
            log.error(strategy.getFailMessage());
        }
    }
    
    /**
     * 이벤트 삭제 로직 실행 메서드
     *
     * <p> 인자로 전달받은 이벤트 삭제 전략에 따라 이벤트 삭제 처리를 진행
     *
     * <p> GAP Lock, DB 부하 및 병목 현상을 방지하기 위한 청크 단위 삭제
     *
     * @param strategy 이벤트 아웃박스 삭제 전략
     */
    private void execute(EventCleanupStrategy strategy) {
        long deletedCount = 0;
        
        while (true) {
            List<Long> ids = strategy.getIdsToDelete();
            
            if (ids.isEmpty()) {
                break;
            }
            
            eventOutboxService.deleteByIds(ids);
            deletedCount += ids.size();
            
            if (ids.size() < strategy.getChunkSize()) {
                break;
            }
        }
        
        log.info("{} Count: {}", strategy.getSuccessMessage(), deletedCount);
    }
}
