package chat.twenty.mapper;

import chat.twenty.domain.RoomMember;
import chat.twenty.dto.UserMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoomMemberMapper {

    RoomMember findById(@Param("roomId") Long roomId, @Param("userId") Long userId);
    List<RoomMember> findAll();
    List<RoomMember> findAllByRoomId(@Param("roomId") Long roomId);
    List<UserMemberDto> findMemberAndUserByRoomId(@Param("roomId") Long roomId);
    Optional<String> findGptUuidByRoomId(@Param("roomId") Long roomId);
    List<RoomMember> findIsTwentyReadyMemberByRoomId(Long roomId);
    int countMemberByRoomId(@Param("roomId") Long roomId);
    int countIsTwentyReadyMemberByRoomId(Long roomId);

    int save(RoomMember roomMember);

    int update(@Param("roomId") Long roomId, @Param("userId") Long userId, RoomMember updateParam);
    int updateIsGptOwner(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isGptOwner") boolean isGptOwner);
    int updateIsRoomOwner(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isRoomOwner") boolean isRoomOwner);
    int updateIsRoomConnected(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isRoomConnected") boolean isRoomConnected);
    int updateIsTwentyGameReady(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isTwentyGameReady") boolean isTwentyReady);
    int updateGptUuid(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("gptUuid") String gptUuid);

    int deleteById(@Param("roomId") Long roomId, @Param("userId") Long userId);


}
