package kr.co.yeogiga.infrastructure.place;

import kr.co.yeogiga.infrastructure.place.dto.PlaceInfoDto;

import java.util.List;

public interface PlaceSearchClient {
    
    /**
     * 특정 장소 키워드를 통한 연관된 장소 검색 메서드
     *
     * @param place     검색할 장소 키워드
     * @return          해당 키워드와 연관된 장소 목록
     */
    List<PlaceInfoDto> fetchPlaces(String place);
}