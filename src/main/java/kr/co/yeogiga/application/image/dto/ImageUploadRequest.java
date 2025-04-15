package kr.co.yeogiga.application.image.dto;

public record ImageUploadRequest(
        byte[] bytes,
        String originalFilename,
        String contentType,
        long size,
        Long tripId
) {
    public static ImageUploadRequest createInstance(byte[] bytes, String originalFilename, String contentType, long size, Long tripId) {
        return new ImageUploadRequest(bytes, originalFilename, contentType, size, tripId);
    }
}
