package chat.twenty.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Map;

/**
 * STOMP 메시지를 가로채서 전 처리하는 인터셉터
 */
@Slf4j
public class StompInterceptor implements ChannelInterceptor {

    /**
     * 메시지 전처리 - client 메시지가 채널에 도착하기 전에 호출
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 참고) log 응답
        /*
        message = GenericMessage [payload=byte[0],
            headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={accept-version=[1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@3a801940, simpSessionId=gdrrtvjy}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
        message = GenericMessage [payload=byte[88],
            headers={simpMessageType=MESSAGE, stompCommand=SEND, nativeHeaders={destination=[/app/gpt/126604], content-length=[88]}, simpSessionAttributes={}, simpHeartbeat=[J@5f964e97, simpSessionId=gdrrtvjy, simpDestination=/app/gpt/126604}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
        message = GenericMessage [payload=byte[0],
            headers={simpMessageType=SUBSCRIBE, stompCommand=SUBSCRIBE, nativeHeaders={id=[sub-0], destination=[/topic/gpt/126604]}, simpSessionAttributes={}, simpHeartbeat=[J@3fb9b1d8, simpSubscriptionId=sub-0, simpSessionId=gdrrtvjy, simpDestination=/topic/gpt/126604}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
        message = GenericMessage [payload=byte[88],
            headers={simpMessageType=MESSAGE, stompCommand=SEND, nativeHeaders={destination=[/app/gpt/126604], content-length=[88]}, simpSessionAttributes={}, simpHeartbeat=[J@3fea5f3b, simpSessionId=gdrrtvjy, simpDestination=/app/gpt/126604}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
        message = GenericMessage [payload=byte[0],
            headers={simpMessageType=DISCONNECT, stompCommand=DISCONNECT, simpSessionAttributes={}, simpHeartbeat=[J@5fc5a074, simpSessionId=gdrrtvjy}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
        message = GenericMessage [payload=byte[0],
            headers={simpMessageType=DISCONNECT, stompCommand=DISCONNECT, simpSessionAttributes={}, simpSessionId=gdrrtvjy}]
            / channel = ExecutorSubscribableChannel[clientInboundChannel]
         */
//        log.info("preSend() message = {} / channel = {} ", message, channel);

        // message.headers
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // connect() 할때 simpSessionAttributes 에 roomId, subUrl 저장
        Boolean containsNativeHeader = Boolean.valueOf(accessor.getFirstNativeHeader("containsNativeHeader"));
        if (containsNativeHeader) {
            Map<String, String> attributeMap = Map.of(
                    "roomId", accessor.getFirstNativeHeader("currentRoomId"),
                    "subUrl", accessor.getFirstNativeHeader("subUrl")
            );
            accessor.getSessionAttributes().putAll(attributeMap);
        }
//
//        // 조작한 header 로 message 재생성
//        Message<?> resultMessage = MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
//        log.info("preSend() result  = {}", resultMessage);
        return message;
    }
}
