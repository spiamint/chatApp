package chat.twenty.repository;

import chat.twenty.domain.RoomMember;
import chat.twenty.dto.UserMemberDto;
import chat.twenty.mapper.RoomMemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ChatRoom 에 소속된 User 관리, key = roomId, value = userId list
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RoomMemberRepository {

    private final RoomMemberMapper memberMapper;

    public RoomMember findById(Long roomId, Long userId) {
        return memberMapper.findById(roomId, userId);
    }

    public List<RoomMember> findAll() {
        return memberMapper.findAll();
    }
    public List<RoomMember> findMemberListByRoomId(Long roomId) {
        return memberMapper.findAllByRoomId(roomId);
    }
    public List<UserMemberDto> findMemberAndUserByRoomId(Long roomId) {
        return memberMapper.findMemberAndUserByRoomId(roomId);
    }
    public Optional<String> findGptUuidByRoomId(Long roomId) {
        return memberMapper.findGptUuidByRoomId(roomId);
    }
    public List<RoomMember> findIsTwentyReadyMemberByRoomId(Long roomId) {
        return memberMapper.findIsTwentyReadyMemberByRoomId(roomId);
    }
    public int countMemberByRoomId(Long roomId) {
        return memberMapper.countMemberByRoomId(roomId);
    }
    public int countTwentyReadyMemberByRoomId(Long roomId) {
        return memberMapper.countIsTwentyReadyMemberByRoomId(roomId);
    }

    public RoomMember save(Long roomId, Long userId) {
        // service 로직으로 별도 분리할 필요가 있을듯?
        RoomMember roomMember = new RoomMember(roomId, userId);
        log.info("saveMember: {}", roomMember);

        RoomMember findMember = findById(roomId, userId);

        // 중복입력방지 ( 방 만들때 한번 들어감. 나중에 고치도록)
        if (findMember != null) {
            log.info("이미 존재하는 멤버입니다.");
            return findMember;
        }

        memberMapper.save(roomMember);
        return memberMapper.findById(roomId, userId);
    }


    public void update(Long roomId, Long userId, RoomMember updateParam) {
        memberMapper.update(roomId, userId, updateParam);
    }

    public void updateIsRoomConnected(Long roomId, Long userId, boolean roomConnected) {
        memberMapper.updateIsRoomConnected(roomId, userId, roomConnected);
    }

    public void updateIsRoomOwner(Long roomId, Long userId, boolean roomOwner) {
        memberMapper.updateIsRoomOwner(roomId, userId, roomOwner);
    }

    public void updateIsGptOwner(Long roomId, Long userId, boolean gptOwner) {
        memberMapper.updateIsGptOwner(roomId, userId, gptOwner);
    }

    public int updateIsTwentyReady(Long roomId, Long userId, boolean twentyGameReady) {
        return memberMapper.updateIsTwentyGameReady(roomId, userId, twentyGameReady);
    }

    public void updateGptUuid(Long roomId, Long userId, String gptUuid) {
        memberMapper.updateGptUuid(roomId, userId, gptUuid);
    }

    public void deleteById(Long roomId, Long userId) {
        memberMapper.deleteById(roomId, userId);
    }

}
