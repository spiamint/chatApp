package chat.twenty.exception;

import lombok.Getter;

/**
 * TwentyGameService.validateOrder() 에서 사용
 */
@Getter
public class TwentyGameOrderNotValidException extends RuntimeException{

    private Long roomId;
    private Long userId;
    private int order; // 현재 Exception 을 발생시킨 순서

    public TwentyGameOrderNotValidException(String message, Long roomId, Long userId, int order) {
        super(message);
        this.order = order;
        this.roomId = roomId;
        this.userId = userId;
    }
}
