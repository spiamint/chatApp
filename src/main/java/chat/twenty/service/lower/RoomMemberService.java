package chat.twenty.service.lower;

import chat.twenty.domain.RoomMember;
import chat.twenty.dto.UserMemberDto;
import chat.twenty.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository repository;

    // public

    public RoomMember findById(Long roomId, Long userId) {
        return repository.findById(roomId, userId);
    }
    public List<RoomMember> findAll() { return repository.findAll(); }
    public List<UserMemberDto> findMemberList(Long roomId) {
        return repository.findMemberAndUserByRoomId(roomId);
    }
    public List<RoomMember> findIsTwentyReadyMemberByRoomId(Long roomId) {
        return repository.findIsTwentyReadyMemberByRoomId(roomId);
    }
    public int countMemberByRoomId(Long roomId) {
        return repository.countMemberByRoomId(roomId);
    }
    public int countTwentyReadyMemberByRoomId(Long roomId) {
        return repository.countTwentyReadyMemberByRoomId(roomId);
    }

    public void enterRoom(Long roomId, Long userId) {
        save(roomId, userId);
    }

    /**
     * (방 생성 후,) 방장으로서 방에 입장
     */
    public void enterRoomByOwner(Long roomId, Long userId) {
        save(roomId, userId);
        updateRoomOwner(roomId, userId, true);
    }

    public void leaveRoom(Long roomId, Long userId) {
        delete(roomId, userId);
    }


    public void leaveRoomAllMember(Long roomId) {
        deleteByRoomId(roomId);
    }

    public void updateRoomConnected(Long roomId, Long userId, boolean roomConnected) {
        repository.updateIsRoomConnected(roomId, userId, roomConnected);
    }

    public void updateRoomOwner(Long roomId, Long userId, boolean roomOwner) {
        repository.updateIsRoomOwner(roomId, userId, roomOwner);
    }

    public void updateGptUuid(Long roomId, Long userId, String gptUuid) {
        repository.updateGptUuid(roomId, userId, gptUuid);
    }

    /**
     * gptOwner 를 update 한 user 의 id 를 반환
     */
    public Long updateGptOwner(Long roomId, Long userId, boolean gptOwner) {
        repository.updateIsGptOwner(roomId, userId, gptOwner);
        return userId;
    }

    public String findGptUuidByRoomId(Long roomId) {
        return repository.findGptUuidByRoomId(roomId).orElse(null);
    }

    public boolean twentyReady(Long roomId, Long userId) {
        return updateTwentyReady(roomId, userId, true) == 1;
    }

    public boolean twentyUnready(Long roomId, Long userId) {
        return updateTwentyReady(roomId, userId, false) == 1;
    }

    public boolean isTwentyAllReady(Long roomId) {
        return countTwentyReadyMemberByRoomId(roomId) == countMemberByRoomId(roomId);
    }


    // protected =======================================================================================

    protected RoomMember save(Long roomId, Long userId) {
        repository.save(roomId, userId);
        return repository.findById(roomId, userId);
    }

    protected void update(Long roomId, Long userId, RoomMember updateParam) {
        repository.update(roomId, userId, updateParam);
    }

    protected int updateTwentyReady(Long roomId, Long userId, boolean twentyReady) {
        return repository.updateIsTwentyReady(roomId, userId, twentyReady);
    }

    protected void delete(Long roomId, Long userId) {
        repository.deleteById(roomId, userId);
    }

    protected int deleteByRoomId(Long roomId) {
        return repository.deleteByRoomId(roomId);
    }
}
