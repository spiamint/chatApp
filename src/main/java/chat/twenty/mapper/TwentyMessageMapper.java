package chat.twenty.mapper;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.TwentyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TwentyMessageMapper {

    TwentyMessage findById(Long id);
    List<TwentyMessage> findAll();
    List<TwentyMessage> findByRoomId(Long roomId);
    List<TwentyMessage> findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    List<TwentyMessage> findGptChatByRoomIdAndGptUuid(@Param("roomId") Long roomId, @Param("gptUuid") String gptUuid);

    void save(TwentyMessage twentyMessage);

    void update(@Param("id") Long id, @Param("updateParam") TwentyMessage updateParam);

    void deleteById(Long id);

}
