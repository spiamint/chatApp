package chat.twenty.event.eventlistener.socket;

import chat.twenty.auth.PrincipalDetails;
import chat.twenty.domain.User;
import chat.twenty.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

/**
 * Session...Event 발생시 실행하는 DefaultEventListener
 * 발생한 이벤트의 subscription url 에 따라 StompWebsocketEvent(User user, Long roomId) 를 재발행
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultWebSocketEventListener {

    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void webSocketConnectListener(SessionConnectedEvent event) {
        // 메시지가 중첩되어있다. 로그확인
        /*
         *event = SessionConnectedEvent[GenericMessage [payload=byte[0],
         * headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0],
         *      headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={currentUsername=[feli], sendUrl=[/app/chat/376398], subUrl=[/topic/chat/376398], accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={currentUsername=feli}, simpHeartbeat=[J@6a20abbd, simpSessionId=wr1hmids}], simpSessionId=wr1hmids}]]
         */
        log.info("WebSocket Connect, event = {}, class = {}", event, event.getClass());

        // CONNECT_ACK 메시지
        Message<byte[]> connectAckMessage = event.getMessage();
        // CONNECT 메시지
        Message<byte[]> connectMessage = (Message<byte[]>) connectAckMessage.getHeaders().get("simpConnectMessage");
        // subUrl
        String subUrl = getSubUrl(connectMessage);
        Long roomId = getRoomId(connectMessage);

        User currentUser = getUserFromEvent(event);
        Object nextEvent = subUrl.contains("chat") ?
                new ChatConnectEvent(currentUser, roomId) : new TwentyConnectEvent(currentUser, roomId);

        eventPublisher.publishEvent(nextEvent);
    }

    /**
     * ConnectEvent 에서 입장메시지를 보내면, Subscribe 전에 보내서 씹히는 경우가 있다.
     */
    @EventListener
    public void webSocketSubscribeListener(SessionSubscribeEvent event) {
        log.info("WebSocket Subcribe, event ={}", event);

        Message<byte[]> subscribeMessage = event.getMessage();
        String subUrl = getSubUrl(subscribeMessage);
        Long roomId = getRoomId(subscribeMessage);

        User currentUser = getUserFromEvent(event);
        StompWebsocketEvent nextEvent = subUrl.contains("chat") ?
                new ChatSubscribeEvent(currentUser, roomId) : new TwentySubscribeEvent(currentUser, roomId);

        eventPublisher.publishEvent(nextEvent);
    }

    // 브라우저의 beforeUnload 이벤트 리스너를 통해 Disconnect 메시지가 전송된다. (이때는 Unsubscribe 발생X)
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("WebSocket DisConnect, event = {} /// message = {}", event, event.getMessage());

        Message<byte[]> disconnectMessage = event.getMessage();
        String subUrl = getSubUrl(disconnectMessage);
        Long roomId = getRoomId(disconnectMessage);

        User currentUser = getUserFromEvent(event);
        StompWebsocketEvent nextEvent = subUrl.contains("chat") ?
                new ChatDisconnectEvent(currentUser, roomId) : new TwentyDisconnectEvent(currentUser, roomId);

        eventPublisher.publishEvent(nextEvent);
    }

    // 사용자가 직접 Unsubscribe 버튼 클릭하면 발생 한다. 이후 Disconnect 요청이 발생할 수있다(수정바람)
    @EventListener
    public void WebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        log.info("WebSocket Unsubscribe, event ={}", event);

        Message<byte[]> unSubscribeMessage = event.getMessage();
        String subUrl = getSubUrl(unSubscribeMessage);
        Long roomId = getRoomId(unSubscribeMessage);

        User currentUser = getUserFromEvent(event);
        StompWebsocketEvent nextEvent = subUrl.contains("chat") ?
                new ChatUnsubscribeEvent(currentUser, roomId) : new TwentyUnsubscribeEvent(currentUser, roomId);

        eventPublisher.publishEvent(nextEvent);
    }

    public String getSubUrl(Message<byte[]> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        return (String) accessor.getSessionAttributes().get("subUrl");
    }

    public Long getRoomId(Message<byte[]> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        return Long.parseLong((String) accessor.getSessionAttributes().get("roomId"));
    }

    /**
     * AbstractSubProtocolEvent (스프링 웹소켓 이벤트 추상클래스) 에서 SpringSecurity 로 로그인된 유저를 반환
     * @return User : twenty.domain.User
     */
    public User getUserFromEvent(AbstractSubProtocolEvent event) {
        // spring.security.authentication.UsernamePasswordAuthenticationToken extends java.security.Principal
        UsernamePasswordAuthenticationToken currentAuthenticationToken = (UsernamePasswordAuthenticationToken) event.getUser();

        PrincipalDetails currentPrincipal = (PrincipalDetails) currentAuthenticationToken.getPrincipal();
        User currentUser = currentPrincipal.getUser();
        return currentUser;
    }

}
