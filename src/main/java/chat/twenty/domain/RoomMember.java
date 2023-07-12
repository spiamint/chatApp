package chat.twenty.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomMember {
    private Long roomId;
    private Long userId;
    private String username;
    @JsonProperty("isRoomConnected")
    private boolean isRoomConnected;
    @JsonProperty("isRoomOwner")
    private boolean isRoomOwner;
    @JsonProperty("isGptOwner")
    private boolean isGptOwner;
    @JsonProperty("isTwentyGameReady")
    private boolean isTwentyGameReady;

    private String gptUuid;     // GPT activate 시의 세션구분. isGptOwner = true 일때 사용


    /**
     * roomId, userId 로 RoomMember 생성
     */
    public RoomMember(Long roomId, Long userId) {
        this.userId = userId;
        this.roomId = roomId;
        this.isRoomConnected = true;
    }

    /**
     * roomId, userId, username 로 RoomMember 생성
     */
    public RoomMember(Long roomId, Long userId, String username) {
        this.userId = userId;
        this.roomId = roomId;
        this.username = username;
        this.isRoomConnected = true;
    }

}
