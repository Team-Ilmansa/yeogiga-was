package kr.co.yeogiga.application.auth.dto;

public class IdInquiryDto {
    public record Request(
            String email
    ) { }
    
    public record Response(
            String username
    ) { }
}
