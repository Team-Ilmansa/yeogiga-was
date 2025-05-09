package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * TripDayPlace 내 이미지 삭제 기능을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TripPlaceImageDeleteService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 단일 이미지를 삭제 메서드
     * - 목적지 내 이미지 삭제 (DeleteType.PLACE)
     * - 기타 항목 이미지 삭제 (DeleteType.UNMATCHED)
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @param imageId        삭제할 이미지의 ID
     * @param deleteReq      삭제 요청 정보 (삭제 타입, Place ID 포함)
     *                       - deleteType PLACE일 경우 placeId 필수
     */
    public void deleteSingleImage(String tripDayPlaceId, String imageId, TripPlaceImageDeleteDto.SingleDeleteReq deleteReq) {
        switch (deleteReq.deleteType()) {
            case PLACE -> tripDayPlaceService.deleteImage(
                    tripDayPlaceId, deleteReq.placeId(), imageId
            );

            case UNMATCHED -> tripDayPlaceService.deleteImageFromUnMatched(
                    tripDayPlaceId, imageId
            );
        }
    }

    /**
     * 특정 여행(tripId)에 속한 모든 TripDayPlace 문서에서
     * 주어진 이미지 ID 리스트에 해당하는 이미지를 일괄 삭제하는 메서드
     *
     * @param tripId    여행 ID
     * @param deleteReq 이미지 ID 리스트가 포함된 삭제 요청 DTO
     */

    public void deleteMultipleImages(Long tripId, TripPlaceImageDeleteDto.MultiDeleteReq deleteReq) {
        tripDayPlaceService.deleteImagesByTripId(tripId, deleteReq.imageIds());
    }
}
