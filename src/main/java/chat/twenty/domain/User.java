package chat.twenty.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@NoArgsConstructor
public class User {

    private Long id;            // id,  auto_increment
    private String username;    // 유저이름 16
    private String loginId;     // 로그인 아이디 16
    private String password;    // 비밀번호 Bcrypt round10, varchar100
    private String role;        // 권한 "ROLE_USER", "ROLE_ADMIN"

    public User(String username) {
        this.username = username;
    }

    /**
     * UserAddForm -> User
     */
    public User(String loginId, String username, String password) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
    }

}
