package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    
    @Nested
    @DisplayName("공지사항 수정")
    class UpdateNotice {
        User user = User.builder()
                .id(1L)
                .nickname("nickname")
                .role(Role.USER)
                .build();
        
        Notice notice = Notice.builder()
                .tripId(2L)
                .title("title")
                .description("description")
                .author(user)
                .build();
        
        NoticeReq.Creation dto = NoticeReq.Creation.builder()
                .title("new title")
                .description("new description")
                .build();
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            ReflectionTestUtils.setField(notice, "authorId", 1L);
            when(noticeService.readById(2L)).thenReturn(Optional.of(notice));
            
            // when
            noticeCommandService.updateNotice(2L, 1L, dto);
            
            // when
            assertEquals(dto.title(), notice.getTitle());
            assertEquals(dto.description(), notice.getDescription());
        }
        
        @Test
        @DisplayName("실패 - 존재하지 않는 공지사항")
        void failIfNoticeNotFound() {
            // given
            when(noticeService.readById(anyLong())).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> noticeCommandService.updateNotice(2L, 1L, dto));
            
            // then
            assertEquals(NoticeErrorType.NOT_FOUND, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 작성자가 아닌 경우")
        void failUnauthorizedAuthor() {
            // given
            ReflectionTestUtils.setField(notice, "authorId", 1L);
            when(noticeService.readById(anyLong())).thenReturn(Optional.of(notice));
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> noticeCommandService.updateNotice(2L, 3L, dto));
            
            // then
            assertEquals(NoticeErrorType.UNAUTHORIZED_AUTHOR, exception.getErrorType());
        }
    }
}
