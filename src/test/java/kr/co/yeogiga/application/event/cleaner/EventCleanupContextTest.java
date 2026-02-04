package kr.co.yeogiga.application.event.cleaner;

import kr.co.yeogiga.application.event.cleaner.strategy.EventCleanupStrategy;
import kr.co.yeogiga.application.event.cleaner.strategy.PublishedEventCleanupStrategy;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventCleanupContextTest {
    @Mock
    private EventOutboxService eventOutboxService;
    
    @InjectMocks
    private EventCleanupContext eventCleanupContext;
    
    private EventCleanupStrategy strategy;
    
    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;
    
    @BeforeEach
    void setUp() {
        strategy = new PublishedEventCleanupStrategy(eventOutboxService);
    }

    @Test
    @DisplayName("성공")
    void success() {
        // given
        when(eventOutboxService.findOldPublishedEventIds(any(LocalDateTime.class), anyLong()))
                .thenReturn(List.of(1L, 2L, 3L, 4L));
        
        // when
        eventCleanupContext.executeSafe(strategy);
        
        // then
        verify(eventOutboxService, times(1)).deleteByIds(idsCaptor.capture());
        
        List<Long> capturedIds = idsCaptor.getValue();
        assertThat(capturedIds).usingRecursiveComparison().isEqualTo(List.of(1L, 2L, 3L, 4L));
    }
    
    @Test
    @DisplayName("성공 - 청크 범위를 벗어날 경우")
    void successDeleteChunk() {
        // given
        List<Long> chunkIds1 = LongStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        List<Long> chunkIds2 = List.of(1001L);
        
        when(eventOutboxService.findOldPublishedEventIds(any(LocalDateTime.class), anyLong()))
                .thenReturn(chunkIds1)
                .thenReturn(chunkIds2);
        
        // when
        eventCleanupContext.executeSafe(strategy);
        
        // then
        verify(eventOutboxService, times(2)).deleteByIds(anyList());
    }
    
    @Test
    @DisplayName("성공 - 청크 범위를 벗어나고, 반환된 리스트의 크기가 청크단위과 동일할 경우")
    void successIfChunkAndListSizeSame() {
        // given
        List<Long> chunkIds1 = LongStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        List<Long> chunkIds2 = LongStream.rangeClosed(1001, 2000).boxed().collect(Collectors.toList());
        
        when(eventOutboxService.findOldPublishedEventIds(any(LocalDateTime.class), anyLong()))
                .thenReturn(chunkIds1)
                .thenReturn(chunkIds2)
                .thenReturn(Collections.emptyList());
        
        // when
        eventCleanupContext.executeSafe(strategy);
        
        // then
        verify(eventOutboxService, times(2)).deleteByIds(anyList());
    }
    
    @Test
    @DisplayName("삭제 중 예외가 발생한 경우")
    void fail() {
        // given
        doThrow(new RuntimeException("Unexpected Error")).when(eventOutboxService).deleteByIds(anyList());
        when(eventOutboxService.findOldPublishedEventIds(any(LocalDateTime.class), anyLong()))
                .thenReturn(List.of(1L));
        
        // when & then
        assertDoesNotThrow(() -> eventCleanupContext.executeSafe(strategy));
        
        verify(eventOutboxService, times(1))
                .findOldPublishedEventIds(any(LocalDateTime.class), anyLong());
    }
}
