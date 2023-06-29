package chat.twenty.repository;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.TwentyMessage;
import chat.twenty.mapper.TwentyMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TwentyMessageRepository {

    private final TwentyMessageMapper twentyMessageMapper;

    public TwentyMessage findById(Long id) {
        return twentyMessageMapper.findById(id);
    }

    public List<TwentyMessage> findAll() {
        return twentyMessageMapper.findAll();
    }

    public List<TwentyMessage> findByRoomId(Long roomId) {
        return twentyMessageMapper.findByRoomId(roomId);
    }

    public List<TwentyMessage> findByRoomIdAndUserId(Long roomId, Long userId) {
        return twentyMessageMapper.findByRoomIdAndUserId(roomId, userId);
    }

    public List<TwentyMessage> findGptChatByRoomIdAndGptUuid(Long roomId, String gptUuid) {
        return twentyMessageMapper.findGptChatByRoomIdAndGptUuid(roomId, gptUuid);
    }

    public TwentyMessage save(TwentyMessage twentyMessage) {
        twentyMessageMapper.save(twentyMessage);
        return twentyMessage;
    }

    public Long update(Long id, TwentyMessage updateParam) {
        twentyMessageMapper.update(id, updateParam);
        return id;
    }

    public void deleteById(Long id) {
        twentyMessageMapper.deleteById(id);
    }

}
