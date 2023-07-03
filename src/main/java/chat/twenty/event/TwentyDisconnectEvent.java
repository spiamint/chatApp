package chat.twenty.event;

import chat.twenty.domain.User;

public class TwentyDisconnectEvent extends StompWebsocketEvent {
    public TwentyDisconnectEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
