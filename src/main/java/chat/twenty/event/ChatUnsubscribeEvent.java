package chat.twenty.event;

import chat.twenty.domain.User;

public class ChatUnsubscribeEvent extends StompWebsocketEvent {

    public ChatUnsubscribeEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
