package chat.twenty.service;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.RoomMember;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.util.*;

/**
 * 스무고개 방 메시지 전처리
 * 전처리 = 사용자에게 받은 메시지를 converAndSend 로 돌려주기 전의 처리
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TwentyMessagePreProcessor {

    private final TwentyGameService twentyService;
    private final RoomMemberService memberService;
    private final CustomGptService gptService;

    public Map<String, Object> preProcessMessage(ChatMessage chatMessage, RoomMember member) {
        boolean isSuccess = true;
        Integer[] orderArray = new Integer[0];
        boolean isTwentyStart = false;
        Long roomId = member.getRoomId();
        Long userId = member.getUserId();

        ChatMessageType messageType = chatMessage.getType();

        switch (messageType) {
            case TWENTY_GAME_READY:
                isSuccess = twentyService.readyGame(roomId, userId);
                break;
            case TWENTY_GAME_UNREADY:
                isSuccess = twentyService.unreadyGame(roomId, userId);
                break;
            case TWENTY_GAME_START:
                isTwentyStart = twentyService.validateGameStart(roomId);
                orderArray = new Integer[memberService.countTwentyReadyMemberByRoomId(roomId)];
                for (int i = 0; i < orderArray.length; i++) {
                    orderArray[i] = i;
                }
                List<Integer> orderList = Arrays.asList(orderArray);
                Collections.shuffle(orderList);
                orderArray = orderList.toArray(new Integer[orderList.size()]); // Object[] -> Integer[]
                if (isTwentyStart) {
                    Long gptOwnerId = gptService.activateGpt(roomId, userId);
                    log.info("preProcessMessage Twenty, TWENTY_GAME_START roomId = {}, userId = {}, gptOwnerId = {}", roomId, userId, gptOwnerId);
                    chatMessage.setGptUuid(memberService.findGptUuidByRoomId(roomId));
                }
                isSuccess = true;
                break;
            default:
                log.info("Twenty preProcessMessage switch default case, messageType = {}", messageType);
                isSuccess = false;
                break;
        }

        return Map.of("isSuccess", isSuccess,
                "orderArray", orderArray,
                "isTwentyStart", isTwentyStart);
    }


}
