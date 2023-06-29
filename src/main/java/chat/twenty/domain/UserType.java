package chat.twenty.domain;

import lombok.Getter;

/**
 * 사전 정의 및 관리될 유저 타입
 */
@Getter
public enum UserType {

    ADMIN(1L, "admin"),             // 관리자
    GPT(3L, "assistant"),                 // User 로써의 GPT
    SYSTEM(4L, "system")            // GPT 질의를 위한 시스템유저
    ;

    private final Long id;          // DB 의 id
    private final String username;

    UserType(Long id, String username) {
        this.id = id;
        this.username = username;
    }



}
