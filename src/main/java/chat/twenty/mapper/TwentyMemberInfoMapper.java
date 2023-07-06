package chat.twenty.mapper;

import chat.twenty.domain.TwentyMemberInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TwentyMemberInfoMapper {
    TwentyMemberInfo findById(Long userId);
    List<TwentyMemberInfo> findByRoomId(Long roomId);
    TwentyMemberInfo findByRoomIdAndOrder(@Param("roomId") Long roomId, @Param("order") int order);
    int save(TwentyMemberInfo twentyMemberInfo);
    int update(@Param("userId") Long userId, @Param("updateParam") TwentyMemberInfo updateParam);
    int updateIsReady(@Param("userId") Long userId, @Param("isReady") boolean isReady);
    int updateIsAlive(@Param("userId") Long userId, @Param("isAlive") boolean isAlive);
    int updateIsAliveAll(@Param("roomId") Long roomId, @Param("isAlive") boolean isAlive);
    int updateOrder(@Param("userId") Long userId, @Param("order") int order);

    int deleteById(Long userId);
    int deleteByRoomId(Long roomId);

}
