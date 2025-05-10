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

    public Optional<User> readIncludeDeletedUserById(Long id) {
        return userRepository.findUserIncludeDeletedById(id);
    }

    public Optional<User> readByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> readIncludeDeletedUserByUsername(String username) {
        return userRepository.findUserIncludeDeletedByUsername(username);
    }

    public Optional<User> readIncludeDeletedUserByPlatformAndPlatformId(OAuthPlatform platform, String platformId) {
        return userRepository.findUserIncludeDeletedByPlatformAndPlatformId(platform, platformId);
    }

    public List<Long> readDeletedUserIdBefore(LocalDate date) {
        return userRepository.findDeletedUserIdBefore(date);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    public void deleteHardAllByIds(List<Long> ids) {
        userRepository.deleteHardAllByIds(ids);
    }
}