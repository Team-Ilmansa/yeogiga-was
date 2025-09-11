package kr.co.yeogiga.presentation.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.application.notice.service.NoticeCommandService;
import kr.co.yeogiga.application.notice.service.NoticeQueryService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = NoticeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class NoticeControllerTest {

    private MockMvc mockMvc;

    private CustomUserDetails userDetails;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeCommandService noticeCommandService;

    @MockBean
    private NoticeQueryService noticeQueryService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .alwaysDo(print())
                .build();

        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("email")
                .role(Role.USER)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        userDetails = new CustomUserDetailsImpl(user);
    }

    @Nested
    @DisplayName("공지사항 생성")
    class CreateNotice {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            NoticeReq.Creation request = NoticeReq.Creation.builder()
                    .title("title")
                    .description("description")
                    .build();

            doNothing().when(noticeCommandService).createNotice(anyLong(), anyLong(), any(NoticeReq.Creation.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/notices", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("실패 - 유효성 검사")
        void failValidation() throws Exception {
            // given
            NoticeReq.Creation request = NoticeReq.Creation.builder()
                    .title(" ")
                    .description(" ")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/notices", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists())
                    .andExpect(jsonPath("$.errors.description").exists());

        }
    }

    @Nested
    @DisplayName("전체 공지사항 조회")
    class getNotices {
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
        void success() throws Exception {
            // given
            when(noticeQueryService.getAllNotices(tripId, pageable)).thenReturn(new PageImpl<>(List.of(noticeDetail)));

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/notices", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content[0].id").value(noticeDetail.id()))
                    .andExpect(jsonPath("$.data.content.length()").value(1));
        }
    }

    @Nested
    @DisplayName("특정 공지사항 조회")
    class GetNotice {
        private final Long noticeId = 1L;

        private NoticeDto.Detail dto = NoticeDto.Detail.builder()
                .id(noticeId)
                .title("title")
                .description("description")
                .authorId(1L)
                .createdAt(LocalDateTime.now().minusDays(1))
                .imageUrl("http://image.com/image")
                .build();


        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(noticeQueryService.getNotice(noticeId)).thenReturn(dto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/notices/{noticeId}", 1L, noticeId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(dto.id()))
                    .andExpect(jsonPath("$.data.authorId").value(dto.authorId()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공지사항")
        void failIfNoticeNotFound() throws Exception {
            // given
            doThrow(new CustomException(NoticeErrorType.NOT_FOUND)).when(noticeQueryService).getNotice(noticeId);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/notices/{noticeId}", 1L, noticeId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(NoticeErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(NoticeErrorType.NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("공지사항 수정")
    class UpdateNotice {

        private NoticeReq.Creation request = NoticeReq.Creation.builder()
                .title("new title")
                .description("new description")
                .build();

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(noticeCommandService).updateNotice(eq(1L), eq(1L), any(NoticeReq.Creation.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/notices/{noticeId}", 2L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 작성자가 아닌 경우")
        void failUnauthorizedAuthor() throws Exception {
            // given
            doThrow(new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR))
                    .when(noticeCommandService).updateNotice(eq(1L), eq(1L), any(NoticeReq.Creation.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/notices/{noticeId}", 2L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(NoticeErrorType.UNAUTHORIZED_AUTHOR.getMessage()));
        }

        @Test
        @DisplayName("실패 - 공지사항이 존재하지 않는 경우")
        void failIfNoticeNotFound() throws Exception {
            // given
            doThrow(new CustomException(NoticeErrorType.NOT_FOUND))
                    .when(noticeCommandService).updateNotice(eq(1L), eq(1L), any(NoticeReq.Creation.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/notices/{noticeId}", 2L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(NoticeErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(NoticeErrorType.NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            NoticeReq.Creation request = NoticeReq.Creation.builder()
                    .title(" ")
                    .description(" ")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/notices/{noticeId}", 2L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(CommonErrorType.VALIDATION_ERROR.getCode()))
                    .andExpect(jsonPath("$.errors.title").exists())
                    .andExpect(jsonPath("$.errors.description").exists());
        }
    }

    @Nested
    @DisplayName("공지사항 상태 변경")
    class UpdateCompleted {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            NoticeReq.UpdateCompleted request = NoticeReq.UpdateCompleted.builder()
                    .completed(true)
                    .build();

            doNothing().when(noticeCommandService).updateCompleted(eq(1L), eq(1L), any(NoticeReq.UpdateCompleted.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/notices/{noticeId}/completed", 2L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }
    }

    @Nested
    @DisplayName("공지사항 삭제")
    class DeleteNotice {
        private final Long noticeId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(noticeCommandService).deleteNotice(noticeId, userDetails.getUserId());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/notices/{noticeId}", 1L, noticeId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공지사항")
        void failIfNoticeNotFound() throws Exception {
            // given
            doThrow(new CustomException(NoticeErrorType.NOT_FOUND)).when(noticeCommandService)
                    .deleteNotice(noticeId, userDetails.getUserId());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/notices/{noticeId}", 1L, noticeId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(NoticeErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(NoticeErrorType.NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 공지사항 작성자가 아닌 경우")
        void failIfUnauthorizedAuthor() throws Exception {
            // given
            doThrow(new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR)).when(noticeCommandService)
                    .deleteNotice(noticeId, userDetails.getUserId());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/notices/{noticeId}", 1L, noticeId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(NoticeErrorType.UNAUTHORIZED_AUTHOR.getCode()))
                    .andExpect(jsonPath("$.message").value(NoticeErrorType.UNAUTHORIZED_AUTHOR.getMessage()));

        }
    }
}
