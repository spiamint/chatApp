package chat.twenty.mapper;

import chat.twenty.domain.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    ChatMessage findById(Long id);
    List<ChatMessage> findAll();
    List<ChatMessage> findByRoomId(Long roomId);
    List<ChatMessage> findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    List<ChatMessage> findGptChatByRoomIdAndGptUuid(@Param("roomId") Long roomId, @Param("gptUuid") String gptUuid);

    void save(ChatMessage chatMessage);

    void update(@Param("id") Long id, @Param("updateParam") ChatMessage updateParam);

    void deleteById(Long id);

}
