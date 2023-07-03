package chat.twenty.controller;

import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.dto.UserMemberDto;
import chat.twenty.service.TwentyMessageDtoProcessor;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TwentyController {

    private final RoomMemberService memberService;
    private final TwentyMessageDtoProcessor twentyMessageDtoProcessor;

    private final SimpMessagingTemplate messageTemplate;


    // /app/twenty-game/{currentRoomId} 로 들어오는 메시지를 처리
    @MessageMapping("/twenty-game/{currentRoomId}")
    public void handleChat(@Header("simpSessionAttributes") Map<String, String> simpSessionAttributes,
                           @Payload TwentyMessageDto twentyMessageDto,
                           @DestinationVariable Long currentRoomId) {
        // simpSessionAttributes 는 ConcurrentHashMap<String, Object> attributes 이지만, <String, String> 으로 받아도 에러가 나지 않는다...
        log.info("handleChat() messageDto = {} currentRoomId = {} simpSessionAttributes = {}, attr class = {}", twentyMessageDto, currentRoomId, simpSessionAttributes, simpSessionAttributes.getClass().getName());

        /**
         * flow 를
         * 1. MessageDto -> Message 변환 및 나머지 필드 입력
         * 2. Message 를 DB 에 저장
         * 3. MessageDto 타입별 처리
         * 4. MessageDto 를 다시 전송
         *  4.1 GPT 질문 처리
         */

        // 메시지 타입별 처리
        TwentyMessageDto resultTwentyMessageDto = twentyMessageDtoProcessor.processMessage(twentyMessageDto);

        // 메시지 처리 결과 되돌려주기
        messageTemplate.convertAndSend("/topic/twenty-game/" + currentRoomId, resultTwentyMessageDto);

        // GameValidate 오류
        if (resultTwentyMessageDto.isTwentyStart()) return;

        // GPT 처리 없음.
        if (!resultTwentyMessageDto.getType().needGptProcess()) return;

        // GPT  ==================================================================

        // GPT PROCESSING 메시지를 gptType = processing 이랑 ChatMEssageType = GPT_PROCESSING 혼재중. 반드시 수정할것
        messageTemplate.convertAndSend("/topic/twenty-game/" + currentRoomId, TwentyMessageDto.createGptProcessingMessage());

        // GPT 질문 처리
        TwentyMessageDto resultGptMessageDto = twentyMessageDtoProcessor.processGpt(twentyMessageDto);

        // GPT 메시지 전송
        messageTemplate.convertAndSend("/topic/twenty-game/" + currentRoomId, resultGptMessageDto);
    }

    @ResponseBody
    @GetMapping("/twenty-game/room/{roomId}/members")
    public Map<String, Object> getMemberList(@PathVariable Long roomId) {
        log.info("getUserList() roomId = {}", roomId);
        List<UserMemberDto> memberList = memberService.findMemberList(roomId);
        log.info("memberList = {}", memberList);
        return Map.of("memberList", memberList);
    }

}
