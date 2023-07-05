package chat.twenty.exception;

import chat.twenty.domain.RoomMember;
import lombok.Getter;

/**
 * TwentyGameService.validateAlive() 에서 사용
 */
@Getter
public class TwentyGameAliveNotValidException extends RuntimeException {

    private RoomMember member;

    public TwentyGameAliveNotValidException(String message, RoomMember member) {
        super(message);
        this.member = member;
    }

}
