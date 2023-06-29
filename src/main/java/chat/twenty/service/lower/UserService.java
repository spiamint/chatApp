package chat.twenty.service.lower;

import chat.twenty.domain.User;
import chat.twenty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User save(User user) {
        // 비밀번호 암호화
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        // 권한설정
        user.setRole("ROLE_USER");

        repository.save(user);
        return repository.findById(user.getId());
    }

    public User update(Long id, User updateParam) {
        repository.update(id, updateParam);
        return repository.findById(id);
    }

    public int deleteById(Long id) {
        return repository.deleteById(id);
    }

    /**
     * 중복 체크
     * @return 존재하면(중복이면) true 반환
     */
    public boolean duplicateCheck(String option, String param) {
        Optional<User> result;

        if (option.equals("username")) {
            result = repository.findByUsername(param);
        } else {
            result = repository.findByLoginId(param);
        }

        return result.isPresent();
    }

}
