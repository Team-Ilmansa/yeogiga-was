package kr.co.yeogiga.application.event.poller;

import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import kr.co.yeogiga.infrastructure.event.publisher.DomainEventExternalPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventOutboxPollerTest {
    @Mock
    private EventOutboxService eventOutboxService;
    
    @Mock
    private DomainEventExternalPublisher eventExternalPublisher;
    
    private EventOutboxPoller poller;
    
    @BeforeEach
    void setUp() {
        Executor directExecutor = Runnable::run; // 메시지 풀이 아닌 임시 동기 Executor 사용
        poller = new EventOutboxPoller(eventOutboxService, eventExternalPublisher, directExecutor);
    }
    
    private List<EventOutbox> eventOutboxes() {
        EventOutbox event1 = mock(EventOutbox.class);
        EventOutbox event2 = mock(EventOutbox.class);
        
        when(event1.getEventId()).thenReturn("EVT-1");
        when(event2.getEventId()).thenReturn("EVT-2");
        when(event1.getEventType()).thenReturn(PasswordResetEvent.class.getName());
        when(event2.getEventType()).thenReturn(EmailVerificationEvent.class.getName());
        when(event1.getPayload()).thenReturn("{'email':'test1@test.com', 'code':'123456'}");
        when(event2.getPayload()).thenReturn("{'email':'test2@test.com', 'code':'654321'}");
        
        return List.of(event1, event2);
    }
    
    @Test
    @DisplayName("성공 - 모든 이벤트 정상 발행")
    void success() {
        // given
        List<EventOutbox> eventOutboxes = eventOutboxes();
        when(eventOutboxService.readAllPollingEventOutbox()).thenReturn(eventOutboxes);
        when(eventExternalPublisher.publishRaw(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(EventPublishResult.success("EVT")));
        
        // when
        poller.poll();
        
        // then
        verify(eventOutboxService, times(1)).updateToPublishedByEventIds(any());
        verify(eventOutboxService, never()).updateToFailedByEventsIds(any());
    }
    
    @Test
    @DisplayName("성공 - 하나는 성공, 하나는 실패")
    void successOneSuccessAndOtherFail() {
        // given
        List<EventOutbox> eventOutboxes = eventOutboxes();
        when(eventOutboxService.readAllPollingEventOutbox()).thenReturn(eventOutboxes);
        when(eventExternalPublisher.publishRaw("EVT-1", PasswordResetEvent.class.getName(), "{'email':'test1@test.com', 'code':'123456'}"))
                .thenReturn(CompletableFuture.completedFuture(EventPublishResult.success("EVT-1")));
        when(eventExternalPublisher.publishRaw("EVT-2", EmailVerificationEvent.class.getName(), "{'email':'test1@test.com', 'code':'123456'}"))
                .thenReturn(CompletableFuture.completedFuture(EventPublishResult.fail("EVT-2", "Failed to publish message")));
        
        // when
        poller.poll();
        
        // then
        verify(eventOutboxService, times(1)).updateToPublishedByEventIds(any());
        verify(eventOutboxService, times(1)).updateToFailedByEventsIds(any());
    }
    
    
    @Test
    @DisplayName("재발행할 이벤트가 존재하지 않는 경우")
    void noEventsToPublish() {
        // given
        when(eventOutboxService.readAllPollingEventOutbox()).thenReturn(Collections.emptyList());
        
        // when
        poller.poll();
        
        // then
        verify(eventOutboxService, never()).updateToPublishedByEventId(any());
        verify(eventOutboxService, never()).updateToFailedByEventsIds(any());
    }
}
