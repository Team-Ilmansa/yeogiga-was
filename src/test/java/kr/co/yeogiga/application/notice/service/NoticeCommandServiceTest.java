package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NoticeCommandServiceTest {
    
    @Mock
    private NoticeService noticeService;
    
    @InjectMocks
    private NoticeCommandService noticeCommandService;
    
    @Nested
    @DisplayName("공지사항 생성")
    class CreateNotice {
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long userId = 1L;
            Long tripId = 2L;
            
            NoticeReq.Creation dto = NoticeReq.Creation.builder()
                    .title("title")
                    .description("description")
                    .build();
            
            doNothing().when(noticeService).save(any(Notice.class));
            
            // when
            noticeCommandService.createNotice(userId, tripId, dto);
            
            // then
            verify(noticeService, times(1)).save(assertArg(notice -> {
                assertEquals(userId, notice.getAuthor().getId());
                assertEquals(tripId, notice.getTripId());
            }));
        }
    }
    
}
