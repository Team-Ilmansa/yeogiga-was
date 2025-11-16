package kr.co.yeogiga.domain.uprisingplace.service;

import kr.co.yeogiga.domain.uprisingplace.entity.UprisingPlace;
import kr.co.yeogiga.domain.uprisingplace.repository.UprisingPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UprisingPlaceService {
    private final UprisingPlaceRepository uprisingPlaceRepository;
    
    public List<UprisingPlace> readAll() {
        return uprisingPlaceRepository.findAll();
    }
}
