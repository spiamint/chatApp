package chat.twenty.dto;

import chat.twenty.enums.ChatMessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Front-Back 간에 메시지 주고받는 DTO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto implements Serializable {

    /**
     * GPT 가 사용할 메시지 타입, 기본값 OFFLINE.
     * 특히, from 이 GPT 가 아니면, 반드시 OFFLINE 상태로 사용하도록함.
     */
    public enum GptType {
        ENTER, CHAT, LEAVE, PROCESSING, OFFLINE
    }
    private String from;
    private String text;
    private String time;
    private int order; // 스무고개 사용. user 는 자신의 order, GPT 는 next order 를 전송
    private ChatMessageType type = ChatMessageType.NONE;
    private GptType gptType = GptType.OFFLINE;    // gptType 은 GPT 메시지 전송 시 사용
    @JsonProperty("isGptChat")
    private boolean isGptChat;      // Gpt 와의 대화인지 여부, 프론트에서 전달
    private String gptUuid;         // GPT 의 UUID, UUID(8)

    @JsonProperty("isTwentyStart")
    private boolean isTwentyStart;     // 스무고개 게임 시작 성공 여부
    private Integer[] orderArray;   // 스무고개 게임 시작 시, 순서를 섞은 배열
    private int twentyNext;         // 스무고개 게임 진행 시, 다음 순서
    private String twentyWinner;   // 스무고개 게임 종료 시, 승자의 이름

    public ChatMessageDto(String from, String text, ChatMessageType type) {
        this.from = from;
        this.text = text;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.type = type;
    }

    public ChatMessageDto(String gptResponse) {
        this.from = "GPT";
        this.text = gptResponse;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.gptType = GptType.CHAT;
    }

    public ChatMessageDto(String gptResponse, GptType gptType) {
        this.from = "GPT";
        this.text = gptResponse;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.gptType = gptType;
    }

    /**
     * from system, 작업 성공여부 (개선필요)
     */
    public ChatMessageDto(ChatMessageType type) {
        this.from = "system";
        this.text = "from system";
        this.type = type;
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
