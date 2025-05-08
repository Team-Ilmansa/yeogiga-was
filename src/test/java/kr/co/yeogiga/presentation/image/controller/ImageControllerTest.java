package kr.co.yeogiga.presentation.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.image.dto.ImageUrlDto;
import kr.co.yeogiga.application.image.service.ImageDeleteProcessor;
import kr.co.yeogiga.application.image.service.ImageUploadProcessor;
import kr.co.yeogiga.application.image.service.TempImageAssignProcessor;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private ImageDeleteProcessor imageDeleteProcessor;

    @MockBean
    private TempImageAssignProcessor tempImageAssignProcessor;

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
                multipart("/api/v1/trip/{tripId}/images/{tripDayPlaceId}", tripId, tripDayPlaceId)
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
                multipart("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/assign", tripId, tripDayPlaceId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("이미지 삭제 테스트")
    void deleteImageTest() throws Exception {
        // given
        List<String> urls = List.of(
                "https://example-image1.com",
                "https://example-image2.com",
                "https://example-image3.com"
        );
        ImageUrlDto imageUrlDto = new ImageUrlDto(urls);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/trip/{tripId}/images", tripId)
                        .content(objectMapper.writeValueAsBytes(imageUrlDto))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }
}
