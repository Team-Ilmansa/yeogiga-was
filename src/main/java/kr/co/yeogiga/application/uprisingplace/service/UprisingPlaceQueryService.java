package kr.co.yeogiga.application.uprisingplace.service;

import kr.co.yeogiga.application.uprisingplace.dto.UprisingPlaceDto;
import kr.co.yeogiga.domain.uprisingplace.service.UprisingPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UprisingPlaceQueryService {
    private final UprisingPlaceService uprisingPlaceService;
    
    /**
     * 인기 급상승 여행지 목록을 조회하는 메서드
     *
     * @return  인기 급상승 여행지 목록
     */
    public List<UprisingPlaceDto.Response> getAllUprisingPlaces() {
        return uprisingPlaceService.readAll().stream()
                .map(UprisingPlaceDto.Response::fromEntity).toList();
    }
}
