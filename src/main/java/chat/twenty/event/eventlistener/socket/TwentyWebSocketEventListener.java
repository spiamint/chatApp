package chat.twenty.event.eventlistener.socket;

import chat.twenty.domain.RoomMember;
import chat.twenty.domain.User;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.event.TwentyConnectEvent;
import chat.twenty.event.TwentyDisconnectEvent;
import chat.twenty.event.TwentySubscribeEvent;
import chat.twenty.event.TwentyUnsubscribeEvent;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import chat.twenty.service.lower.TwentyMemberInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * subUrl /topic/twenty-game/{roomId} 으로 오는 stomp 메시지의 이벤트 처리
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TwentyWebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMemberService memberService;
    private final ChatRoomService roomService;
    private final TwentyMemberInfoService memberInfoService;

    @EventListener
    public void webSocketConnectListener(TwentyConnectEvent event) {
        User currentUser = event.getUser();
        Long currentUserId = currentUser.getId();
        Long currentRoomId = event.getRoomId();

        // 현재 접속한 사용자를 채팅방에 추가
        memberService.enterRoom(currentRoomId, currentUserId);
        // 접속한 사용자의 isRoomConnected = true
        memberService.updateRoomConnected(currentRoomId, currentUserId, true);
    }

    @EventListener
    public void webSocketSubscribeListener(TwentySubscribeEvent event) throws InterruptedException {
        Thread.sleep(100); // 메시지 씹힘 방지를 위해 0.1초 대기
        TwentyMessageDto twentyMessageDto = TwentyMessageDto.createSubscribeMessage(event.getUser().getId(), event.getUser().getUsername());

        // ConnectEvent 에서 입장메시지를 보내면, Subscribe 전에 보내서 씹히는 경우가 있다.
        messagingTemplate.convertAndSend("/topic/twenty-game/" + event.getRoomId(), twentyMessageDto);
    }

    // 브라우저의 beforeUnload 이벤트 리스너를 통해 Disconnect 메시지가 전송된다. (이때는 Unsubscribe 발생X)
    @EventListener
    public void webSocketDisconnectListener(TwentyDisconnectEvent event) {
        User user = event.getUser();
        Long roomId = event.getRoomId();

        RoomMember currentMember = memberService.findById(roomId, user.getId());
        if (currentMember == null) {
            return; /* Unsubscribe 에서 이미 나감 처리된 유저 */
        }

        // 현재 접속한 사용자를 채팅방에서 connected false
        memberService.updateRoomConnected(roomId, user.getId(), false);
        memberService.twentyUnready(roomId, user.getId());

        // 게임중에 나가면, MemberInfo 삭제 (disconnect 가능)
        boolean isPlayerDeleted = false;
        if (roomService.findById(roomId).isGptActivated()) {
            isPlayerDeleted = memberInfoService.deleteByUserId(user.getId());
        }

        TwentyMessageDto twentyMessageDto = TwentyMessageDto.createDisconnectMessage(event.getUser().getId(), user.getUsername(), isPlayerDeleted);
        messagingTemplate.convertAndSend("/topic/twenty-game/" + roomId, twentyMessageDto);

    }

    // 사용자가 직접 Unsubscribe 버튼 클릭하면 발생 한다. 이후 Disconnect 요청이 발생할 수있다(수정바람)
    @EventListener
    public void WebSocketUnsubscribeListener(TwentyUnsubscribeEvent event) {
        User user = event.getUser();
        Long roomId = event.getRoomId();

        // 나간사람이 방장일때
        if (memberService.findById(roomId, user.getId()).isRoomOwner()) {
            memberService.leaveRoomAllMember(roomId); // 방의 모든 member 삭제
            roomService.deleteById(roomId); // 방 삭제
            // 메시지는 일단 삭제하지 않음.
            TwentyMessageDto deleteRoomMessageDto = TwentyMessageDto.createRoomDeleteMessage();
            messagingTemplate.convertAndSend("/topic/twenty-game/" + roomId, deleteRoomMessageDto);
            return;
        }

        // 현재 접속한 사용자가 채팅방에서 나감
        memberService.leaveRoom(roomId, user.getId());

        TwentyMessageDto twentyMessageDto = TwentyMessageDto.createUnsubscribeMessage(user.getId(), user.getUsername());
        messagingTemplate.convertAndSend("/topic/twenty-game/" + roomId, twentyMessageDto);

    }

}
