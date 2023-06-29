package chat.twenty.exception;

import chat.twenty.dto.ChatMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SimpMessagingTemplate messageTemplate;
    private final UserService userService;

    @MessageExceptionHandler(TwentyGameOrderNotValidException.class)
    public void handleTwentyGameOrderNotValidException(TwentyGameOrderNotValidException e) {
        log.info("handleTwentyGameOrderNotValidException() e = {}, e.roomId = {}", e, e.getRoomId());
        ChatMessageDto chatMessageDto = new ChatMessageDto(ChatMessageType.TWENTY_GAME_ERROR);
        chatMessageDto.setText(userService.findById(e.getUserId()).getUsername() + "님 순서를 지켜주세요");
        messageTemplate.convertAndSend("/topic/twenty-game/" + e.getRoomId(), chatMessageDto);
    }

}
