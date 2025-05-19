package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;

import java.util.List;
import java.util.Optional;

public interface CustomTripDayPlaceRepository {
    void savePlace(String id, Place place);
    void saveImage(String id, String placeId, Image image);
    void saveImageToUnmatched(String id, Image image);
    Double findOrderByIdAndPlaceId(String id, String placeId);
    Optional<TripDayPlace> findByIdSorted(String id);
    List<TripDayPlace> findByTripIdSorted(Long tripId);
    Optional<Place> findPlaceByIdAndPlaceId(String id, String placeId);
    List<Image> findUnmatchedImagesById(String id);
    List<TripDayPlace> findTripDayPlaceSummariesByTripId(Long tripId);
    List<Image> findFavoriteImages(String id);
    void updateImageFavorite(String id, String placeId, String imageId, boolean favorite);
    void updatePlaceVisited(String id, String placeId, boolean isVisited);
    void deletePlace(String id, String placeId);
    void deleteImage(String id, String placeId, String imageId);
    void deleteImageFromUnMatched(String id, String imageId);
    void deleteImagesByTripId(Long tripId, List<String> imageIds);
}
