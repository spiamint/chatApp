package chat.twenty.event;

import chat.twenty.domain.User;
import lombok.Getter;
import org.springframework.messaging.MessageHeaders;

@Getter
public class TwentyDisconnectEvent extends StompWebsocketEvent {
    private MessageHeaders headers;
    public TwentyDisconnectEvent(MessageHeaders headers, User user, Long roomId) {
        super(user, roomId);
        this.headers = headers;
    }
}
