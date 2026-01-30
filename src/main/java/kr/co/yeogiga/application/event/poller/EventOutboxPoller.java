package kr.co.yeogiga.application.event.poller;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import kr.co.yeogiga.infrastructure.event.publisher.DomainEventExternalPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOutboxPoller {
    private final EventOutboxService eventOutboxService;
    private final DomainEventExternalPublisher eventExternalPublisher;
    private final Executor messagePublishTaskExecutor;
    
    /**
     * 주기적으로 이벤트 아웃박스를 조회하여, 재발행을 시도하는 폴링 메서드
     *
     * <p> 30초마다 미발행 및 발행 실패 이벤트 아웃박스를 조회하여 재발행 시도
     *
     * <p> 미발행 이벤트의 경우에는 초기 발행 시점과 동시성 문제를 예방 및 이벤트 최대 유효 시간을 고려하여 하고자 생성 후 30초 후, 3분 이내의 이벤트만을 조회
     *
     * <p> 재발행 후 상태 업데이트
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void poll() {
        List<EventOutbox> eventOutboxes = eventOutboxService.readAllPollingEventOutbox();
        
        // 조회한 이벤트 아웃박스들을 발행
        List<CompletableFuture<EventPublishResult>> results = eventOutboxes.stream()
                .map(eventOutbox -> CompletableFuture.supplyAsync(
                                eventPublishTask(eventOutbox),
                                messagePublishTaskExecutor
                        )
                        .thenCompose(future -> future)
                        .exceptionally(ex -> {
                            log.error("Failed to published event \"{}\"", eventOutbox.getEventId(), ex);
                            return EventPublishResult.fail(eventOutbox.getEventId(), ex.getMessage());
                        })
                )
                .toList();
        
        // 모든 이벤트 아웃박스에 대한 메시지 발행이 될 때까지 대기
        CompletableFuture.allOf(results.toArray(new CompletableFuture[0])).join();
        
        // 메시지 발행이 모두 완료되고 난 다음, 이벤트 아웃박스들의 상태를 업데이트
        updateStatus(results);
    }
    
    /**
     * 이벤트 아웃박스 발행 작업을 나타내는 Supplier 메서드
     *
     * @param eventOutbox 이벤트 아웃박스
     * @return 이벤트 메시지 발행 결과 CompletableFuture 객체를 감싸는 Supplier
     */
    private Supplier<CompletableFuture<EventPublishResult>> eventPublishTask(EventOutbox eventOutbox) {
        return () -> eventExternalPublisher.publishRaw(
                eventOutbox.getEventId(), eventOutbox.getEventType(), eventOutbox.getPayload()
        );
    }
    
    /**
     * 이벤트 메시지 발행 결과를 바탕으로 이벤트 아웃박스 상태{@code status}를 업데이트하는 메서드
     *
     * <p> 메서드 호출부에서 모든 이벤트 아웃박스에 대한 메시지 발행이 되기까지 대기하였으므로 결과 객체{@link EventPublishResult}를 즉시 반환
     *
     * <p> 대기 및 실패 이벤트 수를 고려하여 일괄 업데이트 수행
     *
     * @param results 이벤트 메시지 발행 결과 CompletableFuture 리스트
     */
    private void updateStatus(List<CompletableFuture<EventPublishResult>> results) {
        List<String> publishedEventIds = new ArrayList<>();
        List<String> failedEventIds = new ArrayList<>();
        
        results.stream()
                .map(result -> result.join())
                .forEach(result -> {
                    if (result.isSuccess()) {
                        publishedEventIds.add(result.eventId());
                    } else {
                        failedEventIds.add(result.eventId());
                    }
                });
        
        if (!publishedEventIds.isEmpty()) {
            eventOutboxService.updateToPublishedByEventIds(publishedEventIds);
        }
        if (!failedEventIds.isEmpty()) {
            eventOutboxService.updateToFailedByEventsIds(failedEventIds);
        }
    }
}
