package chat.twenty.dto;

import chat.twenty.domain.TwentyMemberInfo;
import chat.twenty.domain.UserType;
import chat.twenty.enums.ChatMessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    private int order;      // 진행시, user 자신의 순서
    private Long nextUserId;         // 진행 시, gpt 가 내려주는 다음 순서의 userId
    private String twentyWinner;   //  종료 시, 승자의 username
    private Long twentyDeadUserId;   // 오답 시 오답 유저의 userId
    private List<TwentyMemberInfo> memberInfoList; // 스무고개 Member 상태(순서) 초기화용 리스트
    private Long banUserId; // 강퇴할 userId
    @JsonProperty("isPlayerLeaved")
    private boolean isPlayerLeaved; // disconnect Leave 메시지에서 플레이중인 유저가 나갔는지 여부(관전자가 아닌지)

    public static TwentyMessageDto createSubscribeMessage(Long userId, String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUserId(userId);
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 입장하셨습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.ENTER);
        return twentyMessageDto;
    }

    public static TwentyMessageDto createDisconnectMessage(Long userId, String username, boolean isPlayerDeleted) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUserId(userId);
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 접속종료 하셨습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.LEAVE);
        twentyMessageDto.isPlayerLeaved = isPlayerDeleted;
        return twentyMessageDto;
    }

    public static TwentyMessageDto createUnsubscribeMessage(Long userId, String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUserId(userId);
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
        // 시간은 DB 저장시 초기화
        return gptAnswerMessage;
    }

    public static TwentyMessageDto createRoomDeleteMessage() {
        TwentyMessageDto deleteMessage = new TwentyMessageDto();
        deleteMessage.setType(ChatMessageType.ROOM_DELETED);
        return deleteMessage;
    }

    public static TwentyMessageDto createAbortMessage(Long roomId, String message) {
        TwentyMessageDto abortMessage = new TwentyMessageDto();
        abortMessage.setType(ChatMessageType.TWENTY_GAME_END);
        abortMessage.setTwentyWinner("");
        abortMessage.setUserId(UserType.SYSTEM.id);
        abortMessage.setRoomId(roomId);
        abortMessage.setUsername("SYSTEM");
        abortMessage.setContent(message);
        return abortMessage;
    }

    public static TwentyMessageDto createBanMessage(Long roomId, Long userId, String username) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setRoomId(roomId);
        twentyMessageDto.setUserId(userId);
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setContent(username + " 님이 강퇴되었습니다.");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));
        twentyMessageDto.setType(ChatMessageType.BAN_MEMBER);
        twentyMessageDto.setBanUserId(userId);
        return twentyMessageDto;
    }

    public static TwentyMessageDto createErrorMessage(String errorMessage) {
        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setUsername("SYSTEM");
        twentyMessageDto.setType(ChatMessageType.ERROR);
        twentyMessageDto.setContent(errorMessage);
        return twentyMessageDto;
    }
}

