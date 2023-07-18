package chat.twenty.domain;

import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.UserType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 기본적인 Message를 상속하는 BaseMessage 클래스
 * GPT 요청 메서드를 일원화 하기 위해 만들었다.
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter // setter 나중에 지워야함
@EqualsAndHashCode
public abstract class BaseMessage {

    protected Long id;        // auto_increment
    protected Long roomId;
    protected Long userId;
    protected String username;    // 조회를 줄이기 위해 username 필드 추가.
    protected ChatMessageType type;       // DB DEFAULT NONE
    protected String content;
    protected LocalDateTime createdAt;  // DB DEFAULT CURRENT_TIMESTAMP

    /**
     * GPT 질문 생성시, role:system 을 만들기 위해 사용하는 메서드, DB 영향 X
     */
    public void setGptSystemRole() {
        this.userId = UserType.SYSTEM.id;
        this.username = UserType.SYSTEM.username;
    }

    /**
     * GPT 질문 생성시, prompt 작성을 위해 사용되는 메서드, DB 영향 X
     */
    public void setGptPrompt(String prompt) {
        this.content = prompt;
    }

}
