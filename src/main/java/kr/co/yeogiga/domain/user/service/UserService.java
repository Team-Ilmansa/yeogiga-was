package kr.co.yeogiga.domain.user.service;

import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> readByPlatformAndPlatformId(OAuthPlatform platform, String platformId) {
        return userRepository.findByPlatformAndPlatformId(platform, platformId);
    }

    public Optional<User> readById(Long id) {
        return userRepository.findById(id);
    }
}