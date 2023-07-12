package chat.twenty.exception;

import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatRoomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(BanMemberNotValidException.class)
    public void handleBanMemberNotValidException(BanMemberNotValidException e) {
        log.info("handleBanMemberNotValidException() e = {}, e.roomId = {}, e.userId = {}, e.banUserId = {}", e, e.getRoomId(), e.getUserId(), e.getBanUserId());

        String destination;
        Object resultMessage;

        if (e.getChatRoomType() == ChatRoomType.TWENTY_GAME) {
            destination = "/topic/twenty-game/" + e.getRoomId();
            resultMessage = TwentyMessageDto.createErrorMessage(e.getMessage());
        } else {
            destination = "/topic/chat/" + e.getRoomId();
            resultMessage = null;
        }

        messagingTemplate.convertAndSend(destination, resultMessage);
    }

}
