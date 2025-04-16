package kr.co.yeogiga.application.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import kr.co.yeogiga.application.image.dto.ImageMetadataDto;
import kr.co.yeogiga.common.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageMetadataService {

    /**
     * 이미지 메타데이터 추출
     *
     * @param bytes            : 이미지의 바이너리 데이터
     * @param originalFilename : 이미지의 이름
     * @return (위도, 경도, 시간) 정보
     */
    public ImageMetadataDto extractMetadata(byte[] bytes, String originalFilename) {
        Double latitude = null;
        Double longitude = null;
        LocalDateTime date = null;

        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            Metadata metadata = ImageMetadataReader.readMetadata(stream);

            Pair<Double, Double> location = extractGeoLocation(metadata);
            latitude = location != null ? location.getLeft() : null;
            longitude = location != null ? location.getRight() : null;

            date = extractTakenDate(metadata);

        } catch (IOException | ImageProcessingException e) {
            log.error("Failed to upload image and extract metadata - filename: {}", originalFilename, e);
        }

        return ImageMetadataDto.of(latitude, longitude, date);
    }

    /**
     * GPS 메타데이터에서 위도/경도를 추출
     *
     * @param metadata : 이미지 메타데이터
     * @return (위도, 경도) Pair 또는 null
     */
    private Pair<Double, Double> extractGeoLocation(Metadata metadata) {
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
            double latitude = gpsDirectory.getGeoLocation().getLatitude();
            double longitude = gpsDirectory.getGeoLocation().getLongitude();
            return Pair.of(latitude, longitude);
        }
        return null;
    }

    /**
     * EXIF 메타데이터에서 촬영 시간을 추출
     *
     * @param metadata : 이미지 메타데이터
     * @return ISO 포맷의 문자열 또는 null
     */
    private LocalDateTime extractTakenDate(Metadata metadata) {
        ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifDirectory != null) {
            Date originalDate = exifDirectory.getDateOriginal();
            return DateTimeUtils.toKoreaLocalDateTime(originalDate);
        }
        return null;
    }
}
