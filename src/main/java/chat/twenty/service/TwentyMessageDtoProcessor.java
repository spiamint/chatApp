package chat.twenty.service;

import chat.twenty.domain.TwentyMessage;
import chat.twenty.dto.MessageDtoMapper;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.TwentyMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 스무고개 방 메시지 타입별 처리를 TwentyGameService 에 위임
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TwentyMessageDtoProcessor {

    private final TwentyGameService twentyService; // twentyService 에만 의존할 것.
    private final TwentyMessageService twentyMessageService; // DB 저장에만 사용

    public TwentyMessageDto processMessage(TwentyMessageDto twentyMessageDto) {
        Long roomId = twentyMessageDto.getRoomId();
        Long userId = twentyMessageDto.getUserId();

        ChatMessageType messageType = twentyMessageDto.getType();
        log.info("START processMessage() TwentyMessageDto = {}", twentyMessageDto);

        switch (messageType) {
            case TWENTY_GAME_READY:
                twentyService.readyGame(roomId, userId);
                break;
            case TWENTY_GAME_UNREADY:
                twentyService.unreadyGame(roomId, userId);
                break;
            case TWENTY_GAME_START:
                boolean isTwentyStart = twentyService.validateGameStart(roomId);
                if (!isTwentyStart) break; // 게임 시작 검증 실패

                // 게임 시작 검증 성공
                twentyMessageDto = twentyService.confirmGameStart(twentyMessageDto);
                break;
            case LEAVE_WHILE_PLAYING:
                twentyMessageDto = twentyService.proceedAbort(roomId, "플레이어가 나갔습니다. 게임이 종료됩니다.");
                break;
            case BAN_MEMBER:
                twentyMessageDto = twentyService.banMember(twentyMessageDto.getRoomId(), twentyMessageDto.getUserId(), twentyMessageDto.getBanUserId());
                break;
            default:
                log.info("Twenty preProcessMessage switch default case, messageType = {}", messageType);
                break;
        }
        // DB 저장 전 시간작성
        twentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        // DB 에 메시지 저장
        TwentyMessage twentyMessage = MessageDtoMapper.INSTANCE.toTwentyMessage(twentyMessageDto);
        twentyMessageService.saveMessage(twentyMessage);

        log.info("FINISH processMessage() TwentyMessageDto = {}", twentyMessageDto);

        return twentyMessageDto;
    }

    public TwentyMessageDto processGpt(TwentyMessageDto twentyMessageDto) {
        TwentyMessageDto resultTwentyMessageDto = null;
        Long roomId = twentyMessageDto.getRoomId();
        Long userId = twentyMessageDto.getUserId();

        ChatMessageType messageType = twentyMessageDto.getType();
        log.info("START processGPT() TwentyMessageDto = {}", twentyMessageDto);

        switch (messageType) {
            case TWENTY_GAME_START:
                resultTwentyMessageDto = twentyService.proceedStart(roomId);
                break;
            case TWENTY_GAME_ASK:
                resultTwentyMessageDto = twentyService.proceedGame(roomId, userId, twentyMessageDto.getOrder());
                resultTwentyMessageDto.setContent(resultTwentyMessageDto.getContent().replace("#&#", ""));
                break;
            case TWENTY_GAME_ANSWER:
                resultTwentyMessageDto = twentyService.proceedAnswer(roomId, twentyMessageDto);
                break;
            default:
                log.info("TwentyMessage processGpt switch default case, messageType = {}", messageType);
                break;
        }

        // DB 저장 전 시간작성
        resultTwentyMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        // DB 에 메시지 저장
        TwentyMessage twentyMessage = MessageDtoMapper.INSTANCE.toTwentyMessage(resultTwentyMessageDto);
        twentyMessageService.saveMessage(twentyMessage);

        log.info("FINISH processGPT() TwentyMessageDto = {}, ResultTwentyMessageDto = {}", twentyMessageDto, resultTwentyMessageDto);

        return resultTwentyMessageDto;
    }


}
