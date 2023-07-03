package chat.twenty.exception;

import lombok.Getter;

/**
 * TwentyGameService.validateOrder() 에서 사용
 */
@Getter
public class TwentyGameOrderNotValidException extends RuntimeException{

    private Long roomId;
    private int order; // 현재 Exception 을 발생시킨 순서

    public TwentyGameOrderNotValidException(String message, int order) {
        super(message);
        this.order = order;
    }
}
