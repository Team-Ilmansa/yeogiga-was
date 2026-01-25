package kr.co.yeogiga.infrastructure.event.dto;

public record EventPublishResult(
        String eventId,
        boolean isSuccess,
        String cause
) {
    public static EventPublishResult success(String eventId) {
        return new EventPublishResult(eventId, true, null);
    }
    
    public static EventPublishResult fail(String eventId, String cause) {
        return new EventPublishResult(eventId, false, cause);
    }
}
