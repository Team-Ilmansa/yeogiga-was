package kr.co.yeogiga.domain.oauth.service;

import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.oauth.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OAuthRepository oauthRepository;

    public OAuth save(OAuth oauth) {
        return oauthRepository.save(oauth);
    }
}