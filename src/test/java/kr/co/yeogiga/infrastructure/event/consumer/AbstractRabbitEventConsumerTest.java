package kr.co.yeogiga.infrastructure.event.consumer;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.domain.event.ExpirableEvent;
import kr.co.yeogiga.infrastructure.event.consumer.support.ProcessedEventStore;
import kr.co.yeogiga.infrastructure.event.exception.ProcessingFailException;
import kr.co.yeogiga.infrastructure.event.exception.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractRabbitEventConsumerTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final String WORK_QUEUE = "test.queue";
    private static final int MAX_RETRY_COUNT = 3;

    @Mock
    private ProcessedEventStore processedEventStore;
    
    @Nested
    @DisplayName("만료된 이벤트 처리")
    class Expiration {

        @Test
        @DisplayName("만료된 ExpirableEvent 는 process/멱등성 검사 없이 즉시 drop 된다")
        void expiredEvent_isDroppedBeforeAnythingElse() {
            // given
            StubExpirableEvent event = new StubExpirableEvent(ZonedDateTime.now(ZONE).minusSeconds(1), true);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.processCount.get()).isZero();
            verify(processedEventStore, never()).isProcessed(any());
            verify(processedEventStore, never()).markProcessed(any(), any());
        }

        @Test
        @DisplayName("만료되지 않은 ExpirableEvent 는 정상 처리된다")
        void notExpiredEvent_isProcessed() {
            // given
            StubExpirableEvent event = new StubExpirableEvent(ZonedDateTime.now(ZONE).plusSeconds(120), false);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.processCount.get()).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("멱등성 처리")
    class Idempotency {

        @Test
        @DisplayName("이미 처리된 이벤트는 process/markProcessed 없이 drop 된다")
        void alreadyProcessedEvent_isDropped() {
            // given
            StubExpirableEvent event = new StubExpirableEvent(ZonedDateTime.now(ZONE).plusSeconds(120), false);
            when(processedEventStore.isProcessed(event)).thenReturn(true);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.processCount.get()).isZero();
            verify(processedEventStore, never()).markProcessed(any(), any());
        }

        @Test
        @DisplayName("처리 성공 시 ExpirableEvent 는 만료까지 남은 시간을 TTL 로 기록한다")
        void success_marksProcessed_withRemainingTtl_forExpirableEvent() {
            // given
            ZonedDateTime expiredAt = ZonedDateTime.now(ZONE).plusSeconds(120);
            StubExpirableEvent event = new StubExpirableEvent(expiredAt, false);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
            verify(processedEventStore, times(1)).markProcessed(eq(event), ttlCaptor.capture());

            Duration ttl = ttlCaptor.getValue();
            assertThat(ttl).isGreaterThan(Duration.ofSeconds(110))
                    .isLessThanOrEqualTo(Duration.ofSeconds(120));
        }

        @Test
        @DisplayName("처리 성공 시 ExpirableEvent 가 아니면 기본 TTL(1일)로 기록한다")
        void success_marksProcessed_withDefaultTtl_forNonExpirableEvent() {
            // given
            TestDomainEvent event = new TestDomainEvent();
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            verify(processedEventStore, times(1)).markProcessed(event, Duration.ofDays(1));
        }

        @Test
        @DisplayName("처리 실패 시에는 markProcessed 를 호출하지 않는다 (재처리 가능)")
        void processFailure_doesNotMarkProcessed() {
            // given
            StubExpirableEvent event = new StubExpirableEvent(ZonedDateTime.now(ZONE).plusSeconds(120), false);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {
                throw new NonRetryableTestException();
            });

            // when
            consumer.handleEvent(event, null);

            // then
            verify(processedEventStore, never()).markProcessed(any(), any());
            assertThat(consumer.deadCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("만료 직전 이벤트로 TTL 이 0 이하가 되면 markProcessed 를 건너뛴다")
        void nonPositiveTtl_skipsMarkProcessed() {
            // given: isExpired() 는 false 지만 expiredAt 이 과거 -> 잔여 TTL 음수
            StubExpirableEvent event = new StubExpirableEvent(ZonedDateTime.now(ZONE).minusSeconds(1), false);
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.processCount.get()).isEqualTo(1);
            verify(processedEventStore, never()).markProcessed(any(), any());
        }
    }
    
    @Nested
    @DisplayName("재시도 / DLQ 처리")
    class RetryAndDead {
        @Test
        @DisplayName("재시도 가능한 예외 & 최대 횟수 미만이면 retry() 호출 (deathCount + 1)")
        void retryableBelowMax_callsRetry() {
            // given
            TestDomainEvent event = new TestDomainEvent();
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {
                throw new RetryableTestException();
            });

            // when
            assertThrows(ProcessingFailException.class, () -> consumer.handleEvent(event, null));

            // then
            assertThat(consumer.retryCount.get()).isEqualTo(1);
            assertThat(consumer.lastDeathCount).isEqualTo(1);
            assertThat(consumer.deadCount.get()).isZero();
        }

        @Test
        @DisplayName("재시도 가능한 예외 & 최대 횟수 도달이면 dead() 호출")
        void retryableReachesMax_callsDead() {
            // given: x-death count 2 -> nextDeathCount 3 == MAX_RETRY_COUNT
            TestDomainEvent event = new TestDomainEvent();
            List<Map<String, Object>> xDeath = List.of(Map.of(
                    "reason", "rejected",
                    "queue", WORK_QUEUE,
                    "count", 2L
            ));
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {
                throw new RetryableTestException();
            });

            // when
            consumer.handleEvent(event, xDeath);

            // then
            assertThat(consumer.deadCount.get()).isEqualTo(1);
            assertThat(consumer.retryCount.get()).isZero();
        }

        @Test
        @DisplayName("재시도 불가능한 예외 -> 즉시 dead 호출")
        void nonRetryableException_callsDead() {
            // given
            TestDomainEvent event = new TestDomainEvent();
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {
                throw new NonRetryableTestException();
            });

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.deadCount.get()).isEqualTo(1);
            assertThat(consumer.retryCount.get()).isZero();
        }

        @Test
        @DisplayName("RetryableException 이 아닌 일반 RuntimeException -> dead 호출")
        void plainRuntimeException_callsDead() {
            // given
            TestDomainEvent event = new TestDomainEvent();
            TestConsumer consumer = new TestConsumer(processedEventStore, () -> {
                throw new IllegalStateException("boom");
            });

            // when
            consumer.handleEvent(event, null);

            // then
            assertThat(consumer.deadCount.get()).isEqualTo(1);
        }
    }

    // ---------------------------------------------------------------------
    // getDeathCount
    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("getDeathCount - x-death 헤더 파싱")
    class GetDeathCount {

        private final TestConsumer consumer = new TestConsumer(processedEventStore, () -> {});

        @Test
        @DisplayName("헤더가 null 이거나 비어있으면 0")
        void nullOrEmpty_returnsZero() {
            assertThat(consumer.getDeathCount(null)).isZero();
            assertThat(consumer.getDeathCount(List.of())).isZero();
        }

        @Test
        @DisplayName("reason=rejected 이고 큐 이름이 일치하는 항목의 count 를 반환한다")
        void matchingEntry_returnsCount() {
            List<Map<String, Object>> xDeath = List.of(Map.of(
                    "reason", "rejected",
                    "queue", WORK_QUEUE,
                    "count", 5L
            ));

            assertThat(consumer.getDeathCount(xDeath)).isEqualTo(5);
        }

        @Test
        @DisplayName("큐 이름이 다르면 0")
        void differentQueue_returnsZero() {
            List<Map<String, Object>> xDeath = List.of(Map.of(
                    "reason", "rejected",
                    "queue", "other.queue",
                    "count", 5L
            ));

            assertThat(consumer.getDeathCount(xDeath)).isZero();
        }
    }

    /** 실제 비즈니스 로직 대신 주입된 동작을 실행하고, retry/dead 호출을 기록하는 테스트용 소비자 */
    static class TestConsumer extends AbstractRabbitEventConsumer<DomainEvent> {
        private final Runnable processBehavior;

        final AtomicInteger processCount = new AtomicInteger();
        final AtomicInteger retryCount = new AtomicInteger();
        final AtomicInteger deadCount = new AtomicInteger();
        volatile int lastDeathCount = -1;

        TestConsumer(ProcessedEventStore processedEventStore, Runnable processBehavior) {
            super(processedEventStore);
            this.processBehavior = processBehavior;
        }

        @Override
        protected String getWorkQueueName() {
            return WORK_QUEUE;
        }

        @Override
        protected int getMaxRetryCount() {
            return MAX_RETRY_COUNT;
        }

        @Override
        protected void process(DomainEvent event) {
            processCount.incrementAndGet();
            processBehavior.run();
        }

        @Override
        protected void retry(DomainEvent event, RuntimeException e, int deathCount) {
            retryCount.incrementAndGet();
            lastDeathCount = deathCount;
            throw new ProcessingFailException(e.getMessage());
        }

        @Override
        protected void dead(DomainEvent event, RuntimeException e) {
            deadCount.incrementAndGet();
        }
    }

    /** ExpirableEvent 가 아닌 일반 도메인 이벤트 */
    static class TestDomainEvent extends DomainEvent {
    }

    /** isExpired() 와 getExpiredAt() 을 독립적으로 제어할 수 있는 ExpirableEvent 스텁 */
    static class StubExpirableEvent extends DomainEvent implements ExpirableEvent {
        private final ZonedDateTime expiredAt;
        private final boolean expired;

        StubExpirableEvent(ZonedDateTime expiredAt, boolean expired) {
            this.expiredAt = expiredAt;
            this.expired = expired;
        }

        @Override
        public ZonedDateTime getExpiredAt() {
            return expiredAt;
        }

        @Override
        public boolean isExpired() {
            return expired;
        }
    }

    static class RetryableTestException extends RetryableException {
        RetryableTestException() {
            super("retryable", null);
        }

        @Override
        public boolean isRetryable() {
            return true;
        }
    }

    static class NonRetryableTestException extends RetryableException {
        NonRetryableTestException() {
            super("non-retryable", null);
        }

        @Override
        public boolean isRetryable() {
            return false;
        }
    }
}
