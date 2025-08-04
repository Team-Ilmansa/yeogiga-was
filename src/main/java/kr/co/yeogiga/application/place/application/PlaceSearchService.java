package kr.co.yeogiga.application.place.application;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.place.exception.PlaceErrorType;
import kr.co.yeogiga.infrastructure.place.PlaceSearchClient;
import kr.co.yeogiga.infrastructure.place.dto.PlaceInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {
    private final PlaceSearchClient placeSearchClient;
    
    /**
     * 특정 키워드를 통해 연관된 장소를 검색하는 메서드
     *
     * @param place     장소 키워드
     * @return          키워드와 연관된 장소 목록
     */
    public List<PlaceInfoDto> searchPlace(String place) {
        if (place.isBlank()) {
            throw new CustomException(PlaceErrorType.NOT_VALID_PLACE_QUERY);
        }
        
        return placeSearchClient.fetchPlaces(place);
    }
}
