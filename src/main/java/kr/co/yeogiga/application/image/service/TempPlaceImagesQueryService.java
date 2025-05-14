package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.TempImageDto;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TempPlaceImagesQueryService {
    private final TempPlaceImagesService tempPlaceImagesService;

    /**
     * 특정 tripDayPlaceId에 해당하는 임시 이미지 목록을 조회하는 메서드
     * - 해당 ID의 문서가 존재하지 않으면 빈 리스트를 반환.
     *
     * @param tripDayPlaceId 여행 일차 ID (TempPlaceImages 문서의 참조 ID)
     * @return Image 도메인을 응답 DTO로 변환한 값의 리스트
     */
    public List<TempImageDto> getTempImagesInfo(String tripDayPlaceId) {
        return tempPlaceImagesService.readByTripDayPlaceId(tripDayPlaceId)
                .map(TempPlaceImages::getImages)
                .orElse(List.of())
                .stream()
                .map(TempImageDto::from)
                .toList();
    }
}
