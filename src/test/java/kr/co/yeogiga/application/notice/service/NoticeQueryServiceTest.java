package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import kr.co.yeogiga.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoticeQueryServiceTest {
    
    @Mock
    private NoticeService noticeService;
    
    @InjectMocks
    private NoticeQueryService noticeQueryService;
    
    @Nested
    @DisplayName("전체 공지사항 조회")
    class GetAllNotices {
        private final Long noticeId = 1L;
        private final Long userId = 2L;
        private final Long tripId = 3L;
        private Pageable pageable = PageRequest.of(0, 10);
        private NoticeDto.Detail noticeDetail = NoticeDto.Detail.builder()
                .id(noticeId)
                .title("title")
                .description("description")
                .nickname("nickname")
                .imageUrl("image")
                .createdAt(LocalDateTime.of(2025, 7, 27, 16, 30))
                .authorId(userId)
                .build();
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(noticeService.readAllNoticeDetailByTripId(tripId, pageable)).thenReturn(new PageImpl<>(List.of(noticeDetail)));
            
            // when
            Page<NoticeDto.Detail> noticeDetails = noticeQueryService.getAllNotices(tripId, pageable);
            
            // then
            assertThat(noticeDetails.getContent()).hasSize(1);
            assertThat(noticeDetails.getContent().get(0)).usingRecursiveComparison().isEqualTo(noticeDetail);
        }
    }
    
    @Nested
    @DisplayName("특정 공지 조회")
    class GetNotice {
        private final Long noticeId = 1L;
        
        private User user = User.builder()
                .nickname("nickname")
                .build();
        
        private Notice notice;
        
        @BeforeEach
        void setUp() {
            notice = Notice.builder()
                    .title("title")
                    .description("description")
                    .tripId(1L)
                    .author(user)
                    .build();
            
            ReflectionTestUtils.setField(user, "imageUrl", "https://image.com/image");
            ReflectionTestUtils.setField(notice, "authorId", 1L);
        }
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(noticeService.readJoinUserById(noticeId)).thenReturn(Optional.of(notice));
            
            // when
            NoticeDto.Detail result = noticeQueryService.getNotice(noticeId);
            
            // then
            assertEquals(notice.getId(), result.id());
            assertEquals(notice.getTitle(), result.title());
            assertEquals(notice.getDescription(), result.description());
            assertEquals(notice.getAuthorId(), result.authorId());
            assertEquals(user.getNickname(), result.nickname());
        }
        
        @Test
        @DisplayName("실패 - 공지사항 미존재")
        void failIfNoticeNotFound() {
            // given
            when(noticeService.readJoinUserById(noticeId)).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                     -> noticeQueryService.getNotice(noticeId));
            
            // then
            assertEquals(NoticeErrorType.NOT_FOUND, exception.getErrorType());
        }
    }
}
