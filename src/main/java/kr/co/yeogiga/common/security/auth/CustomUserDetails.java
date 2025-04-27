package kr.co.yeogiga.common.security.auth;

import kr.co.yeogiga.domain.user.type.Role;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
    Role getRole();
    Long getUserId();
}
