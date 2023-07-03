package chat.twenty.event;

import chat.twenty.domain.User;

public class ChatDisconnectEvent extends StompWebsocketEvent {
    public ChatDisconnectEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
