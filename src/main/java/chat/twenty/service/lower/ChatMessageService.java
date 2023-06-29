package chat.twenty.service.lower;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.RoomMember;
import chat.twenty.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public List<ChatMessage> findAll() {
        return chatMessageRepository.findAll();
    }
    public List<ChatMessage> findCurrentGptQueue(Long roomId, String gptUuid) {
        return chatMessageRepository.findGptChatByRoomIdAndGptUuid(roomId, gptUuid);
    }

    public List<ChatMessage> findByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }

    public List<ChatMessage> findByRoomIdAndUserId(Long roomId, Long userId) {
        return chatMessageRepository.findByRoomIdAndUserId(roomId, userId);
    }

    // 메시지 수정과 삭제는 우선 구현하지 않도록 함.

}
