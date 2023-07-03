package chat.twenty.event;

import chat.twenty.domain.User;

public class ChatConnectEvent extends StompWebsocketEvent {
    public ChatConnectEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
