package chat.twenty.mapper;

import chat.twenty.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class UserMapperTest {

    @Autowired private UserMapper userMapper;

    @Test
    public void findByIdTest() {
        User findUser = userMapper.findById(1L);
        log.info("findUser = {}", findUser);
    }

}
