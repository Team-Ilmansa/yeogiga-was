package kr.co.yeogiga.application.place;

import kr.co.yeogiga.application.place.application.PlaceSearchService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.place.exception.PlaceErrorType;
import kr.co.yeogiga.infrastructure.place.PlaceSearchClient;
import kr.co.yeogiga.infrastructure.place.dto.NaverPlaceInfoDto;
import kr.co.yeogiga.infrastructure.place.dto.PlaceInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaceSearchServiceTest {
    
    @Mock
    private PlaceSearchClient placeSearchClient;
    
    @InjectMocks
    private PlaceSearchService placeSearchService;
    
    @Nested
    @DisplayName("키워드를 통한 장소 검색")
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
        void success() {
            // given
            when(placeSearchClient.fetchPlaces(keyword)).thenReturn(List.of(dto));
            
            // when
            List<PlaceInfoDto> result = placeSearchService.searchPlace(keyword);
            
            // then
            assertThat(result).hasSize(1);
            
            PlaceInfoDto place = result.get(0);
            assertEquals(dto.getTitle(), place.getTitle());
            assertEquals(37.5788408, place.getLatitude());
            assertEquals(126.9770162, place.getLongitude());
        }
        
        @Test
        @DisplayName("실패 - 장소 키워드 누락")
        void failKeyworldNotValid() {
            // given & when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> placeSearchService.searchPlace(" "));
            
            // then
            assertEquals(PlaceErrorType.NOT_VALID_PLACE_QUERY, exception.getErrorType());
        }
    }
    
    
}
