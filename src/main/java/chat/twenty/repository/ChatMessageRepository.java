package chat.twenty.repository;

import chat.twenty.domain.ChatMessage;
import chat.twenty.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ChatMessageRepository {

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessage findById(Long id) {
        return chatMessageMapper.findById(id);
    }

    public List<ChatMessage> findAll() {
        return chatMessageMapper.findAll();
    }

    public List<ChatMessage> findByRoomId(Long roomId) {
        return chatMessageMapper.findByRoomId(roomId);
    }

    public List<ChatMessage> findByRoomIdAndUserId(Long roomId, Long userId) {
        return chatMessageMapper.findByRoomIdAndUserId(roomId, userId);
    }

    public List<ChatMessage> findGptChatByRoomIdAndGptUuid(Long roomId, String gptUuid) {
        return chatMessageMapper.findGptChatByRoomIdAndGptUuid(roomId, gptUuid);
    }

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessageMapper.save(chatMessage);
        return chatMessage;
    }

    public Long update(Long id, ChatMessage updateParam) {
        chatMessageMapper.update(id, updateParam);
        return id;
    }

    public void deleteById(Long id) {
        chatMessageMapper.deleteById(id);
    }

}
