package chat.twenty.event;

import chat.twenty.domain.User;

public class TwentySubscribeEvent extends StompWebsocketEvent {
    public TwentySubscribeEvent(User user, Long roomId) {
        super(user, roomId);
    }
}
