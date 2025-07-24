package kr.co.yeogiga.presentation.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.application.notice.service.NoticeCommandService;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
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
}
