package chat.twenty.repository;

import chat.twenty.domain.ChatRoom;
import chat.twenty.mapper.ChatroomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomRepository {
    private final ChatroomMapper roomMapper;

    public ChatRoom findById(Long id) {
        return roomMapper.findById(id);
    }

    public List<ChatRoom> findAll() {
        return roomMapper.findAll();
    }

    public ChatRoom save(ChatRoom chatRoom) {
        roomMapper.save(chatRoom);
        return chatRoom;
    }

    public Long update(Long id, ChatRoom updateParam) {
        roomMapper.update(id, updateParam);
        return id;
    }

    public Long updateGptActivated(Long id, boolean isGptActivated) {
        roomMapper.updateGptActivated(id, isGptActivated);
        return id;
    }

    /**
     * 스무고개 다음 순서 업데이트
     */
    public boolean updateTwentyNext(Long id, int twentyNext) {
        return roomMapper.updateTwentyNext(id, twentyNext) == 1;
    }

    /**
     * 스무고개 다음순서 0 으로 업데이트(초기화)\
     */
    public boolean updateTwentyNextToZero(Long id) {
        return roomMapper.updateTwentyNextToZero(id) == 1;
    }

    public int deleteById(Long id) {
        return roomMapper.deleteById(id);
    }

}
