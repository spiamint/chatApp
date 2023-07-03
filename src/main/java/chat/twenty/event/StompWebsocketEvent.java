package chat.twenty.event;

import chat.twenty.domain.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StompWebsocketEvent {

    protected User user;
    protected Long roomId;

    public StompWebsocketEvent(User user, Long roomId) {
        this.user = user;
        this.roomId = roomId;
    }
}
