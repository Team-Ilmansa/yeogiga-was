package kr.co.yeogiga.application.event.cleaner;

import jakarta.annotation.PostConstruct;
import kr.co.yeogiga.application.event.cleaner.strategy.EventCleanupStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCleanupJobRegistrar {
    private final List<EventCleanupStrategy> eventCleanupStrategies;
    private final ThreadPoolTaskScheduler eventCleanupTaskScheduler;
    private final EventCleanupContext eventCleanupContext;
    
    @PostConstruct
    public void init() {
        log.info("Register Event Outbox Cleanup Scheduler...");
        for (EventCleanupStrategy strategy : eventCleanupStrategies) {
            try {
                eventCleanupTaskScheduler.schedule(
                    () -> eventCleanupContext.executeSafe(strategy),
                    new CronTrigger(strategy.getCronExpression())
                );
                log.info("\"{}\" scheduling job registered.", strategy.getName());
            } catch (Exception e) {
                log.error("Failed to register EventCleanup scheduling job. strategy = {}.", strategy.getName(), e);
            }
        }
    }
}
