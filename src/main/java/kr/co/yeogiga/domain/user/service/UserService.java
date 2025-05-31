package kr.co.yeogiga.domain.user.service;

import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> readById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> readAllByIds(List<Long> ids) {
        return userRepository.findAllByIds(ids);
    }

    public Optional<User> readIncludeDeletedUserById(Long id) {
        return userRepository.findUserIncludeDeletedById(id);
    }

    public Optional<User> readIncludeDeletedUserByUsername(String username) {
        return userRepository.findUserIncludeDeletedByUsername(username);
    }

    public Optional<User> readIncludeDeletedUserByNickname(String nickname) {
        return userRepository.findUserIncludeDeletedByNickname(nickname);
    }

    public Optional<User> readIncludeDeletedUserByPlatformAndPlatformId(OAuthPlatform platform, String platformId) {
        return userRepository.findUserIncludeDeletedByPlatformAndPlatformId(platform, platformId);
    }

    public List<Long> readDeletedUserIdBefore(LocalDate date) {
        return userRepository.findDeletedUserIdBefore(date);
    }

    public Optional<User> readByFcmToken(String fcmToken) {
        return userRepository.findByFcmToken(fcmToken);
    }

    public boolean existsIncludeDeletedByUsername(String username) {
        return userRepository.findUserIdIncludeDeletedByUsername(username).isPresent();
    }

    public boolean existsIncludeDeletedByNickname(String nickname) {
        return userRepository.findUserIdIncludeDeletedByNickname(nickname).isPresent();
    }

    public boolean existsIncludeDeletedByEmail(String email) {
        return userRepository.findUserIdIncludeDeletedByEmail(email).isPresent();
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    public void deleteHardAllByIds(List<Long> ids) {
        userRepository.deleteHardAllByIds(ids);
    }
}