package chat.twenty.mapper;

import chat.twenty.domain.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatroomMapper {

    ChatRoom findById(Long id);
    List<ChatRoom> findAll();
    int save(ChatRoom chatRoom);
    int update(@Param("id") Long id, @Param("updateParam") ChatRoom chatRoom);
    int updateGptActivated(@Param("id") Long id, @Param("isGptActivated") boolean isGptActivated);
    int updateTwentyNext(@Param("id") Long id);
    int updateTwentyNextToZero(@Param("id") Long id);
    int deleteById(Long id);
}
