package chat.twenty.event;

import chat.twenty.domain.User;

public class TwentyConnectEvent extends StompWebsocketEvent {
    public TwentyConnectEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
