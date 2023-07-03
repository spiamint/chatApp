package chat.twenty.dto;

import chat.twenty.domain.UserType;
import chat.twenty.enums.ChatMessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Front-Back 간에 메시지 주고받는 DTO
 */

@Data
public class TwentyMessageDto {
    private Long roomId;
    private Long userId;
    private ChatMessageType type = ChatMessageType.NONE;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    @JsonProperty("isGptChat") // gptChat 으로 내부사용되는듯? DtoMapper 참조
    private boolean isGptChat;      // Gpt 와의 대화인지 여부, 프론트에서 전달
    private String gptUuid;         // GPT 의 UUID, UUID(8)

    // ====== DTO ONLY ========
    @JsonProperty("isTwentyStart")
    private boolean isTwentyStart;     // 시작시, 시작성공여부
    private Integer[] orderArray;   // 시작시, 순서 정하기 위해 섞은 배열

    private int order;      // 진행시, user 자신의 순서
    private int twentyNext;         // 진행 시, gpt 가 내려주는 다음 순서
    private String twentyWinner;   //  종료 시, 승자의 username

    public static TwentyMessageDto createSubscribeMessage(String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 입장하셨습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.ENTER);
        return twentyMessageDto;
    }

    public static TwentyMessageDto createDisconnectMessage(String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 퇴장하셨습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.LEAVE);
        return twentyMessageDto;
    }

    public static TwentyMessageDto createUnsubscribeMessage(String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 퇴장하셨습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.LEAVE);
        return twentyMessageDto;
    }

    /**
     * GPT_PROCESSING 메시지 생성
     * @return
     */
    public static TwentyMessageDto createGptProcessingMessage() {
        TwentyMessageDto gptProcessingMessage = new TwentyMessageDto();
        gptProcessingMessage.setType(ChatMessageType.GPT_PROCESSING);
        gptProcessingMessage.setContent("GPT 답변 생성중입니다.");
        gptProcessingMessage.setUsername("SYSTEM");
        gptProcessingMessage.setCreatedAt(LocalDateTime.now().withNano(0));
        return gptProcessingMessage;
    }

    /**
     * GPT 의 스무고개 답변 메시지 생성
     */
    public static TwentyMessageDto createGptAnswerMessage(Long roomId, String gptUuid, String gptResponse) {
        TwentyMessageDto gptAnswerMessage = new TwentyMessageDto();
        gptAnswerMessage.setRoomId(roomId);
        gptAnswerMessage.setContent(gptResponse);
        gptAnswerMessage.setGptUuid(gptUuid);

        gptAnswerMessage.setUserId(UserType.GPT.id);
        gptAnswerMessage.setUsername(UserType.GPT.username);
        gptAnswerMessage.setType(ChatMessageType.TWENTY_FROM_GPT);
        gptAnswerMessage.setGptChat(true);

        return gptAnswerMessage;
    }

}

