package chat.twenty.eventlistener;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.RoomMember;
import chat.twenty.domain.UserType;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.MessageDtoMapper;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.ChatMessageService;
import chat.twenty.service.CustomGptService;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Session(Dis)ConnectedEvent 를 처리하는 EventListener
 * ChannelInterCeptor 실행 후에 실행된다.
 * event 객체는 메시지를 포함한다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMemberService memberService;
    private final ChatMessageService chatMessageService;
    private final CustomGptService gptService;

    @EventListener
    public void webSocketConnectListener(SessionConnectedEvent event) {
        // 메시지가 중첩되어있다. 로그확인
        /*
         *event = SessionConnectedEvent[GenericMessage [payload=byte[0],
         * headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0],
         *      headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={currentUsername=[feli], sendUrl=[/app/chat/376398], subUrl=[/topic/chat/376398], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={currentUsername=feli}, simpHeartbeat=[J@6a20abbd, simpSessionId=wr1hmids}], simpSessionId=wr1hmids}]]
         */
        log.info("WebSocket Connect, event = {}", event);

        // CONNECT_ACK 메시지, 사용X
        // StompHeaderAccessor accessorOuter = StompHeaderAccessor.wrap(event.getMessage());

        // CONNECT 메시지
        Message<?> messageContent = (Message<?>) (event.getMessage().getHeaders().get("simpConnectMessage"));
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(messageContent);
//        log.info("messageContent headers = {}", accessor.getMessageHeaders());

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes.isEmpty()) {
            // sessionAttributes 없음 -> 멤버저장 X
            return;
        }

        Long currentUserId = Long.parseLong(sessionAttributes.get("currentUserId").toString());
        Long currentRoomId = Long.parseLong(sessionAttributes.get("currentRoomId").toString());

        // 현재 접속한 사용자를 채팅방에 추가
        memberService.enterRoom(currentRoomId, currentUserId);
        // 접속한 사용자의 isRoomConnected = true
        memberService.updateRoomConnected(currentRoomId, currentUserId, true);
    }

    /**
     * ConnectEvent 에서 입장메시지를 보내면, Subscribe 전에 보내서 씹히는 경우가 있다.
     */
    @EventListener
    public void webSocketSubscribeListener(SessionSubscribeEvent event) {
        log.info("WebSocket Subcribe, event ={}", event);

        // CONNECT 메시지
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        SessionAttr sessionAttr = convertSessionAttr(accessor.getSessionAttributes());

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setFrom("SYSTEM");
        chatMessageDto.setText(sessionAttr.getUsername() + " 님이 입장하셨습니다.");
        chatMessageDto.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        chatMessageDto.setType(ChatMessageType.ENTER);

        if (sessionAttr.getSubUrl() == null) {
            // subUrl = null
            return;
        }

        messagingTemplate.convertAndSend(sessionAttr.getSubUrl(), chatMessageDto);
    }

    // 브라우저의 beforeUnload 이벤트 리스너를 통해 Disconnect 메시지가 전송된다. (이때는 Unsubscribe 발생X)
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("WebSocket DisConnect, event = {} /// message = {}", event, event.getMessage());

        // CONNECT 메시지
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        SessionAttr sessionAttr = convertSessionAttr(accessor.getSessionAttributes());
        
        // 현재 접속한 사용자를 채팅방에서 connected false
        memberService.updateRoomConnected(sessionAttr.getRoomId(), sessionAttr.getUserId(), false);
        memberService.twentyUnready(sessionAttr.getRoomId(), sessionAttr.getUserId());

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setFrom("SYSTEM");
        chatMessageDto.setText(sessionAttr.getUsername() + " 님 접속종료.");
        chatMessageDto.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        chatMessageDto.setType(ChatMessageType.LEAVE);

        if (sessionAttr.getSubUrl() == null) {
            // subUrl = null
            return;
        }

        // 접속종료 메시지 전달
        messagingTemplate.convertAndSend(sessionAttr.getSubUrl(), chatMessageDto);

        RoomMember currentMember = memberService.findById(sessionAttr.getRoomId(), sessionAttr.getUserId());

        if (currentMember == null) { return; /* Unsubscribe 에서 이미 나감 처리된 유저 */}

        if (currentMember.isGptOwner()) {
            // disconnect 한 현재 유저가 gptOwner 이면, gpt 비활성화
            gptService.deActivateGpt(sessionAttr.getRoomId(), sessionAttr.getUserId());

            // DB 에 GPT 답변 저장
            ChatMessage gptChatMessage = new ChatMessage(sessionAttr.getRoomId(), UserType.GPT.getId(), ChatMessageType.GPT_LEAVE,
                    UserType.GPT.getUsername(), "GPT 가 비활성화 되었습니다.", true, sessionAttr.getGptUuid());
            ChatMessage savedChatMessage = chatMessageService.saveMessage(gptChatMessage);
            log.info("savedMessage = {}", savedChatMessage);

            // GPT 답변 Message -> MessageDto 로 변환 후 채팅방에 전송
            ChatMessageDto gptResult = MessageDtoMapper.INSTANCE.toMessageDto(gptChatMessage);
            messagingTemplate.convertAndSend("/topic/chat/" + sessionAttr.getRoomId(), gptResult);
        }
    }

    // 사용자가 직접 Unsubscribe 버튼 클릭하면 발생 한다. 이후 Disconnect 요청이 발생할 수있다(수정바람)
    @EventListener
    public void WebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        log.info("WebSocket Unsubscribe, event ={}", event);

        // CONNECT 메시지
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        SessionAttr sessionAttr = convertSessionAttr(accessor.getSessionAttributes());

        // 현재 접속한 사용자가 채팅방에서 나감
        memberService.leaveRoom(sessionAttr.getRoomId(), sessionAttr.getUserId());

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setFrom("SYSTEM");
        chatMessageDto.setText(sessionAttr.getUsername() + " 님이 퇴장하셨습니다.");
        chatMessageDto.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        chatMessageDto.setType(ChatMessageType.ENTER);

        if (sessionAttr.getSubUrl() == null) {
            // subUrl = null or Empty
            return;
        }

        messagingTemplate.convertAndSend(sessionAttr.getSubUrl(), chatMessageDto);
    }

    /**
     * Map<String, Object> SessionAttributes 를 SessionAttr 로 변환
     * 필드가 더 많아지면 리플렉션 사용을 고려?
     */
    public SessionAttr convertSessionAttr(Map<String, Object> sessionAttributes) {
        if (sessionAttributes == null || sessionAttributes.isEmpty()) {
            // SessionAttribute 없음
            log.info("sessionAttributes is null or empty, sessionAttributes = {}", sessionAttributes);
            return SessionAttr.builder().build();
        }
        return SessionAttr.builder()
                .username(sessionAttributes.get("currentUsername").toString())
                .userId(Long.parseLong(sessionAttributes.get("currentUserId").toString()))
                .roomId(Long.parseLong(sessionAttributes.get("currentRoomId").toString()))
                .subUrl(sessionAttributes.get("subUrl").toString())
                .gptUuid((String)sessionAttributes.get("gptUuid"))
                .build();
    }

}
