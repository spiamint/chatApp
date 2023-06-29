package chat.twenty.mapper;

import chat.twenty.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
     User findById(Long id);
     Optional<User> findByUsername(String username);
     Optional<User> findByLoginId(String loginId);
     List<User> findAll();
     int save(User user);
     int update(@Param("id") Long id, @Param("updateParam") User user);
     int deleteById(Long id);
}
