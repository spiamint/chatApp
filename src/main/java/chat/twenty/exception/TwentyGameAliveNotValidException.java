package chat.twenty.exception;

import lombok.Getter;

/**
 * TwentyGameService.validateAlive() 에서 사용
 */
@Getter
public class TwentyGameAliveNotValidException extends RuntimeException {

    private Long roomId;
    private Long userId;

    public TwentyGameAliveNotValidException(String message, Long roomId, Long userId) {
        super(message);
        this.roomId = roomId;
        this.userId = userId;
    }

}
