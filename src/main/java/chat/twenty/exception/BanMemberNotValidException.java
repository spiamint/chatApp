package chat.twenty.exception;

import chat.twenty.enums.ChatRoomType;
import lombok.Getter;

@Getter
public class BanMemberNotValidException extends RuntimeException {
    private Long roomId;
    private Long userId;
    private Long banUserId;
    private ChatRoomType chatRoomType;

    public BanMemberNotValidException(String message, Long roomId, Long userId, Long banUserId, ChatRoomType chatRoomType) {
        super(message);
        this.roomId = roomId;
        this.userId = userId;
        this.banUserId = banUserId;
        this.chatRoomType = chatRoomType;
    }
}
