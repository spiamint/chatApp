package chat.twenty.service.lower;

import chat.twenty.domain.TwentyMessage;
import chat.twenty.repository.TwentyMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwentyMessageService {

    private final TwentyMessageRepository twentyMessageRepository;

    public TwentyMessage saveMessage(TwentyMessage twentyMessage) {
        twentyMessage.setTime();
        return twentyMessageRepository.save(twentyMessage);
    }

    public TwentyMessage findById(Long id) {
        return twentyMessageRepository.findById(id);
    }

    public List<TwentyMessage> findAll() {
        return twentyMessageRepository.findAll();
    }
    public List<TwentyMessage> findCurrentGptQueue(Long roomId, String gptUuid) {
        return twentyMessageRepository.findGptChatByRoomIdAndGptUuid(roomId, gptUuid);
    }

    public List<TwentyMessage> findByRoomId(Long roomId) {
        return twentyMessageRepository.findByRoomId(roomId);
    }

    public List<TwentyMessage> findByRoomIdAndUserId(Long roomId, Long userId) {
        return twentyMessageRepository.findByRoomIdAndUserId(roomId, userId);
    }

    // 메시지 수정과 삭제는 우선 구현하지 않도록 함.

}
