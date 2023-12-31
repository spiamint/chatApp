package chat.twenty.domain;

import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.UserType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatMessage extends BaseMessage{
    private boolean isGptChat;      // Gpt 와의 대화인지 여부, DB DEFAULT false
    private String gptUuid;    // GPT activate 시의 세션구분. UUID(8)

    /**
     * 일반채팅 생성자
     */
    public ChatMessage(Long roomId, Long userId, ChatMessageType type, String content) {
        this.roomId = roomId;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    /**
     * GPT 옵션 활성화 채팅 생성자
     */
    public ChatMessage(Long roomId, Long userId, ChatMessageType type, String username, String content, boolean isGptChat, String gptUuid) {
        this.roomId = roomId;
        this.userId = userId;
        this.username = username;
        this.type = type;
        this.content = content;
        this.isGptChat = isGptChat;
        this.gptUuid = gptUuid;
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
