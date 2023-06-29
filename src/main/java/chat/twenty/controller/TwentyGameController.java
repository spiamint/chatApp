package chat.twenty.controller;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.RoomMember;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.MessageDtoMapper;
import chat.twenty.dto.UserMemberDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.CustomGptService;
import chat.twenty.service.MessagePreProcessor;
import chat.twenty.service.TwentyGameService;
import chat.twenty.service.TwentyMessagePreProcessor;
import chat.twenty.service.lower.ChatMessageService;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TwentyGameController {

    private final RoomMemberService memberService;
    private final ChatMessageService chatMessageService;
    private final TwentyGameService twentyGameService;
//    private final CustomGptService gptService;

//    private final MessagePreProcessor messagePreProcessor;
    private final TwentyMessagePreProcessor twentyMessagePreProcessor;
    private final SimpMessagingTemplate messageTemplate;

    // /app/twenty-game/{roomId} 로 들어오는 메시지를 처리
    @MessageMapping("/twenty-game/{roomId}")
    public void handleChat(@Header("simpSessionAttributes") Map<String, String> simpSessionAttributes,
                           @Payload ChatMessageDto chatMessageDto,
                           @DestinationVariable Long roomId) {
        // simpSessionAttributes 는 ConcurrentHashMap<String, Object> attributes 이지만, <String, String> 으로 받아도 에러가 나지 않는다...
        log.info("handleChat() messageDto = {} roomId = {} simpSessionAttributes = {}, attr class = {}", chatMessageDto, roomId, simpSessionAttributes, simpSessionAttributes.getClass().getName());

        Long currentUserId = Long.parseLong(simpSessionAttributes.get("currentUserId"));
        int currentTwentyOrder;

        // MessageDto -> Message 변환 및 나머지 필드 입력
        ChatMessage chatMessage = MessageDtoMapper.INSTANCE.toMessage(chatMessageDto);
        chatMessage.setUserId(currentUserId);
        chatMessage.setRoomId(roomId);
        chatMessage.setCreatedAt(LocalDateTime.now().withNano(0));
        log.info("handleChat() message = {}", chatMessage);

        // 메시지 타입별 전처리
        ChatMessageType chatMessageType = chatMessage.getType();
        RoomMember currentMember = memberService.findById(roomId, currentUserId);
        Map<String, Object> preProcessResult = twentyMessagePreProcessor.preProcessMessage(chatMessage, currentMember);

        boolean isPreProcessSuccess = (boolean) preProcessResult.get("isSuccess");
        Integer[] orderArray = (Integer[]) preProcessResult.get("orderArray"); // null or orderArray
        boolean isTwentyStart = (boolean) preProcessResult.get("isTwentyStart");

        // DB 저장 및 전송
        chatMessageService.saveMessage(chatMessage);
        ChatMessageDto resultChatMessageDto = MessageDtoMapper.INSTANCE.toMessageDto(chatMessage);

        // 채팅 메시지 돌려주기
        if (chatMessageType.needResend()) {
            messageTemplate.convertAndSend("/topic/twenty-game/" + roomId, resultChatMessageDto);
        }

        // 시스템 메시지 돌려주기 , 메시지 수정 필요할듯
        if (chatMessageType.needResendSystem()) {
            ChatMessageType answerType = chatMessageType; // 들어온 타입 그대로 답변 타입으로 사용
            resultChatMessageDto = new ChatMessageDto(answerType);

            if (chatMessageType == ChatMessageType.TWENTY_GAME_START) {
                // TWENTY_GAME_START 에서만 사용
                resultChatMessageDto.setTwentyStart(isTwentyStart);
                resultChatMessageDto.setOrderArray(orderArray);
                resultChatMessageDto.setGptUuid(chatMessage.getGptUuid());
            }

            messageTemplate.convertAndSend("/topic/twenty-game/" + roomId, resultChatMessageDto);
        }

        if (chatMessageType == ChatMessageType.TWENTY_GAME_START) {
            // TWENTY_GAME_START 에서만 사용
            if (!isTwentyStart) {
                // GameValidate 오류
                return;
            }
        }

        if (chatMessageType == ChatMessageType.CHAT || chatMessageType ==  ChatMessageType.TWENTY_GAME_READY
            || chatMessageType == ChatMessageType.TWENTY_GAME_UNREADY) { /* 일반 유저 대 유저 채팅이면 여기서 종료 */ return; }

        // GPT 처리 시작 ==================================================================

        // GPT PROCESSING 메시지를 gptType = processing 이랑 ChatMEssageType = GPT_PROCESSING 혼재중. 반드시 수정할것
        ChatMessageDto gptProcessingMessage = new ChatMessageDto();
        gptProcessingMessage.setType(ChatMessageType.GPT_PROCESSING);
        gptProcessingMessage.setText("GPT 답변 생성중입니다.");
        gptProcessingMessage.setFrom("SYSTEM");
        messageTemplate.convertAndSend("/topic/twenty-game/" + roomId, gptProcessingMessage);

        currentTwentyOrder = chatMessageDto.getOrder();
        Map<String, Object> proceedGameResult = twentyGameService.proceedGame(currentTwentyOrder, currentMember, chatMessage);
        ChatMessage gptResponseChatMessage = (ChatMessage) proceedGameResult.get("gptResponse");
        int nextOrder = (int) proceedGameResult.get("nextOrder");

        // GPT 답변 DB 저장
        ChatMessage savedChatMessage = chatMessageService.saveMessage(gptResponseChatMessage);
        log.info("handleChat() saved gptResponseMessage = {}", savedChatMessage);

        // GPT 답변 Message -> MessageDto 로 변환 후 채팅방에 전송
        ChatMessageDto gptResult = MessageDtoMapper.INSTANCE.toMessageDto(gptResponseChatMessage);
        gptResult.setOrder(nextOrder); // 다음순서 설정

        if (gptResult.getType() == ChatMessageType.TWENTY_GAME_END) {
            gptResult.setTwentyWinner(chatMessageDto.getFrom()); // 승자 닉네임 설정
        }

        messageTemplate.convertAndSend("/topic/twenty-game/" + roomId, gptResult);
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
