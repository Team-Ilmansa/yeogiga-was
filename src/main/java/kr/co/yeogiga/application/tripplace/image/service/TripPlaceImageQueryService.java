package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.application.tripplace.image.dto.FavoriteImageRes;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 여행 일차의 장소 이미지 관련 정보를 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
public class TripPlaceImageQueryService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 특정 TripDayPlace 내에서 지정된 Place의 정보와 해당 장소에 속한 이미지 목록을 조회 메서드
     *
     * @param tripDayPlaceId TripDayPlace(여행 일차) ID
     * @param placeId        조회할 Place(장소) ID
     * @return 해당 장소의 기본 정보와 이미지 목록이 포함된 응답 DTO
     */
    public TripPlaceImageRes.PlaceImageInfo getPlaceImageInfo(String tripDayPlaceId, String placeId) {
        Place place = tripDayPlaceService.readPlaceByIdAndPlaceId(tripDayPlaceId, placeId)
                .orElseThrow(() -> new CustomException(TripErrorType.PLACE_NOT_FOUND));

        return TripPlaceImageRes.PlaceImageInfo.from(place);
    }

    /**
     * TripDayPlace의 unmatchedImages (어느 장소에도 연결되지 않은 기타 이미지)를 조회 메서드
     *
     * @param tripDayPlaceId TripDayPlace(여행 일차) ID
     * @return unmatchedImages 목록이 포함된 응답 DTO
     */
    public TripPlaceImageRes.UnmatchedImageInfo getUnmatchedImageInfo(String tripDayPlaceId) {
        List<Image> images = tripDayPlaceService.readUnmatchedImagesById(tripDayPlaceId);

        return TripPlaceImageRes.UnmatchedImageInfo.from(images);
    }

    /**
     * TripDayPlace에서 즐겨찾기(favorite)로 표시된 이미지 목록 조회 메서드
     * - 목적지에 매핑된 이미지 & 기타 이미지(unmatchedImages) 중 favorite == true 인 이미지만 포함
     *
     * @param tripDayPlaceId TripDayPlace(여행 일차) ID
     * @return 즐겨찾기된 이미지 목록 (FavoriteImageRes DTO 리스트)
     */
    public List<FavoriteImageRes> getFavoriteImages(String tripDayPlaceId) {
        return tripDayPlaceService.readFavoriteImages(tripDayPlaceId).stream()
                .map(FavoriteImageRes::from)
                .toList();
    }
}
