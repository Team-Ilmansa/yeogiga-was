package kr.co.yeogiga.presentation.place.controller;

import kr.co.yeogiga.application.place.application.PlaceSearchService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.PlaceErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import kr.co.yeogiga.infrastructure.place.dto.NaverPlaceInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PlaceController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class PlaceControllerTest {
    
    private MockMvc mockMvc;
        
    private CustomUserDetails userDetails;
    
    @MockBean
    private PlaceSearchService placeSearchService;
    
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
    @DisplayName("장소 검색")
    class SearchPlace {
        private final String keyword = "경복궁";
        
        private NaverPlaceInfoDto dto = new NaverPlaceInfoDto(
                "경복궁",
                "https://mock.com",
                "경복궁입니다.",
                "02-000-0000",
                "서울특별시 종로구 세종로 1-1 경복궁",
                "서울특별시 종로구 사직로 161 경복궁",
                "1269770162",
                "375788408"
        );
        
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(placeSearchService.searchPlace(keyword)).thenReturn(List.of(dto));
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/places/search")
                            .queryParam("place", keyword)
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].title").value(dto.getTitle()))
                    .andExpect(jsonPath("$.data[0].latitude").value(37.5788408))
                    .andExpect(jsonPath("$.data[0].longitude").value(126.9770162));
        }
        
        @Test
        @DisplayName("실패 - 키워드 유효성 검증 실패")
        void failKeywordNotValid() throws Exception {
            // given
            doThrow(new CustomException(PlaceErrorType.NOT_VALID_PLACE_QUERY)).when(placeSearchService).searchPlace(keyword);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/places/search")
                            .queryParam("place", keyword)
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(PlaceErrorType.NOT_VALID_PLACE_QUERY.getCode()))
                    .andExpect(jsonPath("$.message").value(PlaceErrorType.NOT_VALID_PLACE_QUERY.getMessage()));
        }
    }
}
