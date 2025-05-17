package kr.co.yeogiga.presentation.route.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.route.dto.RouteReq;
import kr.co.yeogiga.application.route.dto.RouteRes;
import kr.co.yeogiga.application.route.service.TripLeaderCommandService;
import kr.co.yeogiga.application.route.service.TripRouteQueryService;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.triproute.entity.Route;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RouteController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripLeaderCommandService tripLeaderCommandService;

    @MockBean
    private TripRouteQueryService tripRouteQueryService;

    private final Long tripId = 1L;
    private final int day = 1;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("방장 경로 저장 테스트")
    void storeLeaderRouteTest() throws Exception {
        // given
        RouteReq.Request routeReq =
                new RouteReq.Request(37.123456, 127.123456);

        // whe
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/trip/{tripId}/days/{day}/routes", tripId, day)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routeReq))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("여행 루트 조회 테스트")
    void getTripRoutesTest() throws Exception {
        // given
        Route route1 = Route.builder().latitude(0.0).longitude(1.1).build();
        Route route2 = Route.builder().latitude(2.2).longitude(3.3).build();

        RouteRes routeRes1 = new RouteRes(1, List.of(route1));
        RouteRes routeRes2 = new RouteRes(2, List.of(route2));

        List<RouteRes> routeList = List.of(routeRes1, routeRes2);

        given(tripRouteQueryService.getTripRoutes(tripId)).willReturn(routeList);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/routes", tripId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].routes[0].latitude").value(route1.getLatitude()))
                .andExpect(jsonPath("$.data[1].routes[0].latitude").value(route2.getLatitude()));
    }
}
