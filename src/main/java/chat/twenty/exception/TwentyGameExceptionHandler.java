package chat.twenty.exception;

import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class TwentyGameExceptionHandler {

    private final SimpMessagingTemplate messageTemplate;

    @MessageExceptionHandler(TwentyGameOrderNotValidException.class)
    public void handleTwentyGameOrderNotValidException(TwentyGameOrderNotValidException e) {
        log.info("handleTwentyGameOrderNotValidException() e = {}, e.roomId = {}", e, e.getRoomId());

        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setType(ChatMessageType.TWENTY_GAME_ERROR);
        twentyMessageDto.setOrder(e.getOrder()); // 순서를 지키지 않은 유저의 순서
        twentyMessageDto.setContent(" 님 순서를 지켜주세요");

        messageTemplate.convertAndSend("/topic/twenty-game/" + e.getRoomId(), twentyMessageDto);
    }

    @MessageExceptionHandler(TwentyGameAliveNotValidException.class)
    public void handleTwentyGameAliveNotValidException(TwentyGameAliveNotValidException e) {
        log.info("handleTwentyGameOrderNotValidException() e = {}, e.roomId = {} e.userId = {}", e, e.getRoomId(), e.getUserId());

        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();
        twentyMessageDto.setType(ChatMessageType.TWENTY_GAME_ERROR);
        twentyMessageDto.setUserId(e.getUserId()); // 죽은 유저의 id
        twentyMessageDto.setContent(" 님 부정 질문");

        messageTemplate.convertAndSend("/topic/twenty-game/" + e.getRoomId(), twentyMessageDto);
    }

}
