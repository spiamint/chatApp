package chat.twenty.exception;

import lombok.Getter;

/**
 * TwentyGameService.validateOrder() 에서 사용
 */
@Getter
public class TwentyGameOrderNotValidException extends RuntimeException{

    private Long roomId;
    private Long userId;

    public TwentyGameOrderNotValidException(String message, Long roomId, Long userId) {
        super(message);
        this.roomId = roomId;
        this.userId = userId;
    }
}
