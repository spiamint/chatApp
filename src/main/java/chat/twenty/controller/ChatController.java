package chat.twenty.controller;

import chat.twenty.auth.PrincipalDetails;
import chat.twenty.domain.RoomMember;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.service.ChatMessageDtoProcessor;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RoomMemberService memberService;

    private final SimpMessagingTemplate messageTemplate;
    private final ChatMessageDtoProcessor chatMessageDtoProcessor;


    // /app/chat/{roomId} 로 들어오는 메시지를 처리
    @MessageMapping("/chat/{roomId}")
    public void handleChat(@Header("simpSessionAttributes") Map<String, String> simpSessionAttributes,
                           @Payload ChatMessageDto chatMessageDto,
                           @DestinationVariable Long roomId) {
        // simpSessionAttributes 는 ConcurrentHashMap<String, Object> attributes 이지만, <String, String> 으로 받아도 에러가 나지 않는다...
        log.info("handleChat() messageDto = {} roomId = {} simpSessionAttributes = {}, attr class = {}", chatMessageDto, roomId, simpSessionAttributes, simpSessionAttributes.getClass().getName());


        // 메시지 타입별 전처리
        ChatMessageDto resultChatMessageDto = chatMessageDtoProcessor.processMessage(chatMessageDto);

        // 채팅 메시지 돌려주기
        messageTemplate.convertAndSend("/topic/chat/" + roomId, resultChatMessageDto);

        if (!resultChatMessageDto.getType().needGptProcess()) { /* GPT 처리 필요 없으면 바로종료 */ return; }

        // GPT 질의 처리 ====================================================

        messageTemplate.convertAndSend("/topic/chat/" + roomId, ChatMessageDto.createGptProcessingMessage());

        ChatMessageDto gptResultChatMessageDto = chatMessageDtoProcessor.processGpt(chatMessageDto);

        messageTemplate.convertAndSend("/topic/chat/" + roomId, gptResultChatMessageDto);

    }

    @ResponseBody
    @GetMapping("/chat/room/{roomId}/members")
    public Map<String, Object> getMemberList(@PathVariable Long roomId) {
        log.info("getUserList() roomId = {}", roomId);
        List<RoomMember> memberList = memberService.findMemberList(roomId);
        log.info("memberList = {}", memberList);
        return Map.of("memberList", memberList);
    }

    @ResponseBody
    @GetMapping("/chat/room/{roomId}/gpt-owner-check")
    public Map<String, Object> checkGptOwner(@PathVariable Long roomId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("checkGptOwner() roomId = {}", roomId);
        RoomMember findMember = memberService.findById(roomId, principalDetails.getUser().getId());
        return Map.of("isGptOwner", findMember.isGptOwner());
    }
}

