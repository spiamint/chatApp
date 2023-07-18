package chat.twenty.domain;

import chat.twenty.enums.UserType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder // 부모클래스 포함 빌더
public class TwentyMessage extends BaseMessage{

    private boolean isGptChat;      // Gpt 와의 대화인지 여부, DB DEFAULT false
    private String gptUuid;    // GPT activate 시의 세션구분. UUID(8)

    public void setTime() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

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
