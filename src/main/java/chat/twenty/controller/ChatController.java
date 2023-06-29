package chat.twenty.controller;

import chat.twenty.auth.PrincipalDetails;
import chat.twenty.domain.*;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.MessageDtoMapper;
import chat.twenty.dto.UserMemberDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.*;
import chat.twenty.service.lower.ChatMessageService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static chat.twenty.dto.ChatMessageDto.GptType;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final CustomGptService gptService;
    private final RoomMemberService memberService;
    private final ChatMessageService chatMessageService;

    private final SimpMessagingTemplate messageTemplate;
    private final MessagePreProcessor messagePreProcessor;


    // /app/chat/{roomId} 로 들어오는 메시지를 처리
    @MessageMapping("/chat/{roomId}")
    public void handleChat(@Header("simpSessionAttributes") Map<String, String> simpSessionAttributes,
                           @Payload ChatMessageDto chatMessageDto,
                           @DestinationVariable Long roomId) {
        // simpSessionAttributes 는 ConcurrentHashMap<String, Object> attributes 이지만, <String, String> 으로 받아도 에러가 나지 않는다...
        log.info("handleChat() messageDto = {} roomId = {} simpSessionAttributes = {}, attr class = {}", chatMessageDto, roomId, simpSessionAttributes, simpSessionAttributes.getClass().getName());

        Long currentUserId = Long.parseLong(simpSessionAttributes.get("currentUserId"));
        
        // MessageDto -> Message 변환 및 나머지 필드 입력
        ChatMessage chatMessage = MessageDtoMapper.INSTANCE.toMessage(chatMessageDto);
        chatMessage.setUserId(currentUserId);
        chatMessage.setRoomId(roomId);
        chatMessage.setCreatedAt(LocalDateTime.now().withNano(0));
        log.info("handleChat() message = {}", chatMessage);

        // 메시지 타입별 전처리
        ChatMessageType chatMessageType = chatMessage.getType();
        RoomMember currentMember = memberService.findById(roomId, currentUserId);
        messagePreProcessor.preProcessMessage(chatMessage, currentMember);

        // DB 저장 및 전송
        chatMessageService.saveMessage(chatMessage);
        ChatMessageDto resultChatMessageDto = MessageDtoMapper.INSTANCE.toMessageDto(chatMessage);

        // 채팅 메시지 돌려주기
        if (chatMessageType.needResend()) {
            messageTemplate.convertAndSend("/topic/chat/" + roomId, resultChatMessageDto);
        }

        if (chatMessageType == ChatMessageType.CHAT) { /* 일반 유저 대 유저 채팅이면 여기서 종료 */ return; }

        // GPT 질의 처리 ====================================================

        messageTemplate.convertAndSend("/topic/chat/" + roomId,
                new ChatMessageDto("GPT 가 답변을 생성하는 중입니다...", ChatMessageDto.GptType.PROCESSING));

        // GPT 질의 및 답변수신
        ChatMessage gptResponseChatMessage = gptService.sendGptRequest(chatMessageType, currentMember);

        // GPT 답변 DB 저장
        ChatMessage savedChatMessage = chatMessageService.saveMessage(gptResponseChatMessage);
        log.info("handleChat() saved gptResponseMessage = {}", savedChatMessage);

        // GPT 답변 Message -> MessageDto 로 변환 후 채팅방에 전송
        ChatMessageDto gptResult = MessageDtoMapper.INSTANCE.toMessageDto(gptResponseChatMessage);
        messageTemplate.convertAndSend("/topic/chat/" + roomId, gptResult);

    }

    @ResponseBody
    @GetMapping("/chat/room/{roomId}/members")
    public Map<String, Object> getMemberList(@PathVariable Long roomId) {
        log.info("getUserList() roomId = {}", roomId);
        List<UserMemberDto> memberList = memberService.findMemberList(roomId);
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

