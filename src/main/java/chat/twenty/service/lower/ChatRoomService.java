package chat.twenty.service.lower;

import chat.twenty.domain.ChatRoom;
import chat.twenty.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository repository;

    public ChatRoom findById(Long id) {
        return repository.findById(id);
    }

    public List<ChatRoom> findAll() {
        return repository.findAll();
    }

    public ChatRoom save(ChatRoom chatRoom) {
        repository.save(chatRoom);
        return repository.findById(chatRoom.getId());
    }

    public Long update(Long id, ChatRoom updateParam) {
        return id;
    }

    public Long updateGptActivated(Long id, boolean isGptActivated) {
        return repository.updateGptActivated(id, isGptActivated);
    }

    /**
     * 스무고개 순서 업데이트
     */
    public boolean updateNextTwentyOrder(Long id, int twentyNext) {
        return repository.updateTwentyNext(id, twentyNext);
    }

    /**
     * 스무고개 순서 초기화
     */
    public boolean resetTwentyOrder(Long id) {
        return repository.updateTwentyNextToZero(id);
    }

    public int deleteById(Long id) {
        return repository.deleteById(id);
    }
}
