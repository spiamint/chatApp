package chat.twenty.exception;

import chat.twenty.domain.User;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class TwentyGameExceptionHandler {

    private final SimpMessagingTemplate messageTemplate;
    private final UserService userService;

    //TWENTY_GAME_ERROR 로 nextUserId 를 내려주지 않도록 한다.
    //  그래야 프론트에서 쓰던 nextUserId 를 그대로 사용해서 처리.

    @MessageExceptionHandler(TwentyGameOrderNotValidException.class)
    public void handleTwentyGameOrderNotValidException(TwentyGameOrderNotValidException e) {
        log.info("handleTwentyGameOrderNotValidException() e = {}, e.roomId = {}, e.userId = {}", e, e.getRoomId(), e.getUserId());

        User findUser = userService.findById(e.getUserId());

        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setType(ChatMessageType.TWENTY_GAME_ERROR);
        twentyMessageDto.setContent(findUser.getUsername() + " 님 순서를 지켜주세요");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        messageTemplate.convertAndSend("/topic/twenty-game/" + e.getRoomId(), twentyMessageDto);
    }

    @MessageExceptionHandler(TwentyGameAliveNotValidException.class)
    public void handleTwentyGameAliveNotValidException(TwentyGameAliveNotValidException e) {
        log.info("handleTwentyGameOrderNotValidException() e = {}, e.roomId = {} e.userId = {}", e, e.getRoomId(), e.getUserId());

        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setType(ChatMessageType.TWENTY_GAME_ERROR);
        twentyMessageDto.setContent(" 님 부정 질문");
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        messageTemplate.convertAndSend("/topic/twenty-game/" + e.getRoomId(), twentyMessageDto);
    }

}
