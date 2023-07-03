package chat.twenty.event;

import chat.twenty.domain.User;

public class ChatSubscribeEvent extends StompWebsocketEvent {

    public ChatSubscribeEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
