package kr.co.yeogiga.infrastructure.mail.constant;

public final class MailConstant {
    private MailConstant() { }
    
    private static final String VERIFICATION_CODE_PREFIX_FORMAT = "email-verification-code:%s";
    
    public static String formatVerificationCodePrefix(String email) {
        return VERIFICATION_CODE_PREFIX_FORMAT.formatted(email);
    }
}
