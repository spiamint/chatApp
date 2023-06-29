package chat.twenty.repository;

import chat.twenty.domain.User;
import chat.twenty.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepository {
    private final UserMapper userMapper;

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Optional<User> findByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User save(User user) {
        userMapper.save(user);
        return user;
    }

    public Long update(Long id, User updateParam) {
        userMapper.update(id, updateParam);
        return id;
    }

    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

}
