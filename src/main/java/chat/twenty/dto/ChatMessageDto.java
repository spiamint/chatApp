package chat.twenty.dto;

import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Front-Back 간에 메시지 주고받는 DTO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    private Long roomId;
    private Long userId;
    private ChatMessageType type = ChatMessageType.NONE;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    @JsonProperty("isGptChat")
    private boolean isGptChat;      // Gpt 와의 대화인지 여부, 프론트에서 전달
    private String gptUuid;         // GPT 의 UUID, UUID(8)

    public static ChatMessageDto createSubscribeMessage(Long userId, String username) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setUserId(userId); // 프론트 에서 처리할때 필요
        chatMessageDto.setUsername("SYSTEM");
        chatMessageDto.setContent(username + " 님이 입장하셨습니다.");
        chatMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        chatMessageDto.setType(ChatMessageType.ENTER);
        return chatMessageDto;
    }

    public static ChatMessageDto createDisconnectMessage(Long userId, String username) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setUserId(userId); // 프론트 에서 처리할때 필요
        chatMessageDto.setUsername("SYSTEM");
        chatMessageDto.setContent(username + " 님이 접속종료 하였습니다.");
        chatMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        chatMessageDto.setType(ChatMessageType.LEAVE);
        return chatMessageDto;
    }

    public static ChatMessageDto createUnsubscribeMessage(Long userId, String username) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setUserId(userId); // 프론트 에서 처리할때 필요
        chatMessageDto.setUsername("SYSTEM");
        chatMessageDto.setContent(username + " 님이 퇴장하셨습니다.");
        chatMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        chatMessageDto.setType(ChatMessageType.LEAVE);
        return chatMessageDto;
    }

    public static ChatMessageDto createGptLeaveMessage(Long roomId) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setUserId(UserType.GPT.id);
        chatMessageDto.setRoomId(roomId);
        chatMessageDto.setType(ChatMessageType.GPT_LEAVE);
        chatMessageDto.setContent("GPT 가 비활성화 되었습니다.");
        chatMessageDto.setUsername("SYSTEM");
        chatMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        return chatMessageDto;
    }

    /**
     * GPT_PROCESSING 메시지 생성
     */
    public static ChatMessageDto createGptProcessingMessage() {
        ChatMessageDto gptProcessingMessage = new ChatMessageDto();
        gptProcessingMessage.setType(ChatMessageType.GPT_PROCESSING);
        gptProcessingMessage.setContent("GPT 답변 생성중입니다.");
        gptProcessingMessage.setUsername("SYSTEM");
        gptProcessingMessage.setCreatedAt(LocalDateTime.now().withNano(0));
        return gptProcessingMessage;
    }

    /**
     * GPT 의 채팅 답변 메시지 생성
     */
    public static ChatMessageDto createGptAnswerMessage(Long roomId, String gptUuid, String gptResponse) {
        ChatMessageDto gptAnswerMessage = new ChatMessageDto();
        gptAnswerMessage.setRoomId(roomId);
        gptAnswerMessage.setContent(gptResponse);
        gptAnswerMessage.setGptUuid(gptUuid);

        gptAnswerMessage.setUserId(UserType.GPT.id);
        gptAnswerMessage.setUsername(UserType.GPT.username);
        gptAnswerMessage.setType(ChatMessageType.CHAT_FROM_GPT);
        gptAnswerMessage.setGptChat(true);

        return gptAnswerMessage;
    }
}


/*
STOMP message Json body Example
{
    "from": "John",
    "text": "Hello!"
    "time": "2021-08-11T15:00:00"
    "order": "1"
    "option":
}
*/
