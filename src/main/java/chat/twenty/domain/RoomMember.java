package chat.twenty.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomMember {
    private Long roomId;
    private Long userId;
    private boolean isRoomConnected;
    private boolean isRoomOwner;
    private boolean isGptOwner;
    private boolean isTwentyGameReady;
    private boolean isTwentyAlive;

    private String gptUuid;     // GPT activate 시의 세션구분. isGptOwner = true 일때 사용


    /**
     * roomId, userId 로 RoomMember 생성
     */
    public RoomMember(Long roomId, Long userId) {
        this.userId = userId;
        this.roomId = roomId;
        this.isRoomConnected = true;
    }

}
