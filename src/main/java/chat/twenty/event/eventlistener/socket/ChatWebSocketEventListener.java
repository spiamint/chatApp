package chat.twenty.event.eventlistener.socket;

import chat.twenty.domain.RoomMember;
import chat.twenty.domain.User;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.event.ChatConnectEvent;
import chat.twenty.event.ChatDisconnectEvent;
import chat.twenty.event.ChatSubscribeEvent;
import chat.twenty.event.ChatUnsubscribeEvent;
import chat.twenty.service.gpt.CustomGptService;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * subUrl /topic/chat/{roomId} 으로 오는 stomp 메시지의 이벤트 처리
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMemberService memberService;
    private final CustomGptService gptService;
    private final ChatRoomService roomService;


    @EventListener
    public void webSocketConnectListener(ChatConnectEvent event) {
        User user = event.getUser();
        Long roomId = event.getRoomId();

        // 현재 접속한 사용자를 채팅방에 추가
        memberService.enterRoom(roomId, user.getId());
        // 접속한 사용자의 isRoomConnected = true
        memberService.updateRoomConnected(roomId, user.getId(), true);
    }

    @EventListener
    public void webSocketSubscribeListener(ChatSubscribeEvent event) throws InterruptedException {
        Thread.sleep(100); // 메시지 씹힘 방지를 위해 0.1초 대기
        ChatMessageDto chatMessageDto = ChatMessageDto.createSubscribeMessage(event.getUser().getId(), event.getUser().getUsername());
        // ConnectEvent 에서 입장메시지를 보내면, Subscribe 전에 보내서 씹히는 경우가 있다.
        messagingTemplate.convertAndSend("/topic/chat/" + event.getRoomId(), chatMessageDto);
    }

    // 브라우저의 beforeUnload 이벤트 리스너를 통해 Disconnect 메시지가 전송된다. (이때는 Unsubscribe 발생X)
    @EventListener
    public void webSocketDisconnectListener(ChatDisconnectEvent event) {
        Long roomId = event.getRoomId();
        Long userId = event.getUser().getId();

        RoomMember currentMember = memberService.findById(roomId, userId);
        if (currentMember == null) { return; /* Unsubscribe 에서 이미 나감 처리된 유저 */}
        
        // 현재 접속한 사용자를 채팅방에서 connected false
        memberService.updateRoomConnected(roomId, userId, false);

        // 현재 접속한 사용자가 gptOwner 이면, gpt 비활성화
        if (currentMember.isGptOwner()) {
            gptService.deActivateGpt(roomId, userId);
            ChatMessageDto gptLeaveMessage = ChatMessageDto.createGptLeaveMessage(roomId);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, gptLeaveMessage);
        }

        ChatMessageDto chatMessageDto = ChatMessageDto.createDisconnectMessage(userId, event.getUser().getUsername());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessageDto);

        // =================

    }

    // 사용자가 직접 Unsubscribe 버튼 클릭하면 발생 한다. 이후 Disconnect 요청이 발생할 수있다(수정바람)
    @EventListener
    public void WebSocketUnsubscribeListener(ChatUnsubscribeEvent event) {
        Long userId = event.getUser().getId();
        Long roomId = event.getRoomId();
        RoomMember currentMember = memberService.findById(roomId, userId);

        // 나간사람이 방장일때
        if(memberService.findById(roomId, userId).isRoomOwner()) {
            memberService.leaveRoomAllMember(roomId); // 방의 모든 member 삭제
            roomService.deleteById(roomId); // 방 삭제
            // 메시지는 일단 삭제하지 않음.
            TwentyMessageDto deleteRoomMessageDto = TwentyMessageDto.createRoomDeleteMessage();
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, deleteRoomMessageDto);
            return;
        }

        // 현재 접속한 사용자가 gptOwner 이면, gpt 비활성화
        if (currentMember.isGptOwner()) {
            gptService.deActivateGpt(roomId, userId);
            ChatMessageDto gptLeaveMessage = ChatMessageDto.createGptLeaveMessage(roomId);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, gptLeaveMessage);
        }

        // 현재 접속한 사용자가 채팅방에서 나감
        memberService.leaveRoom(roomId, userId);

        ChatMessageDto chatMessageDto = ChatMessageDto.createUnsubscribeMessage(userId, event.getUser().getUsername());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessageDto);
    }

}
