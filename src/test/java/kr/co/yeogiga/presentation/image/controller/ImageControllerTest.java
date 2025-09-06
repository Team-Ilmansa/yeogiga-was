package kr.co.yeogiga.presentation.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.image.dto.ImageDeleteDto;
import kr.co.yeogiga.application.image.dto.TempImageDto;
import kr.co.yeogiga.application.image.service.ImageUploadProcessor;
import kr.co.yeogiga.application.image.service.TempImageAssignProcessorLegacy;
import kr.co.yeogiga.application.image.service.TempPlaceImagesCommandService;
import kr.co.yeogiga.application.image.service.TempPlaceImagesQueryService;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ImageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageUploadProcessor imageUploadProcessor;

    @MockBean
    private TempImageAssignProcessorLegacy tempImageAssignProcessorLegacy;

    @MockBean
    private TempPlaceImagesCommandService tempPlaceImagesCommandService;

    @MockBean
    private TempPlaceImagesQueryService tempPlaceImagesQueryService;

    private final Long tripId = 1L;
    private final String tripDayPlaceId = "trip-day-place-id";

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("이미지 업로드 테스트")
    void uploadImagesTest() throws Exception {
        // given
        MockMultipartFile mockImage = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images", tripId, tripDayPlaceId)
                        .file(mockImage)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("이미지 매칭 테스트")
    void assigneImageTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/assign", tripId, tripDayPlaceId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("임시 이미지 리스트 조회 테스트")
    void getTempImagesInfoTest() throws Exception {
        // given
        List<TempImageDto> imageDtos = List.of(
                new TempImageDto("image1-id", "https://image1.com"),
                new TempImageDto("image2-id", "https://image2.com")
        );

        given(tempPlaceImagesQueryService.getTempImagesInfo(tripDayPlaceId))
                .willReturn(imageDtos);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/temp-images", tripId, tripDayPlaceId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."))
                .andExpect(jsonPath("$.data[0].id").value("image1-id"))
                .andExpect(jsonPath("$.data[1].id").value("image2-id"));
    }

    @Test
    @DisplayName("이미지 삭제 테스트")
    void deleteImageTest() throws Exception {
        // given
        List<String> imageIds = List.of("image1-id", "image2-id");
        List<String> urls = List.of("https://image1.com", "https://image2.com");

        ImageDeleteDto deleteDto = new ImageDeleteDto(imageIds, urls);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/trip/{tripId}/day-place/temp-images/{tripDayPlaceId}}", tripId, tripDayPlaceId)
                        .content(objectMapper.writeValueAsBytes(deleteDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }
}
