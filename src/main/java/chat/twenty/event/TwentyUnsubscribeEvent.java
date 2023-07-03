package chat.twenty.event;

import chat.twenty.domain.User;

public class TwentyUnsubscribeEvent extends StompWebsocketEvent {

    public TwentyUnsubscribeEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
