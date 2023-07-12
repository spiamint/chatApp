package chat.twenty.service.lower;

import chat.twenty.domain.TwentyMemberInfo;
import chat.twenty.repository.TwentyMemberInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwentyMemberInfoService {
    private final TwentyMemberInfoRepository repository;

    public TwentyMemberInfo findById(Long userId) {
        return repository.findById(userId);
    }

    public List<TwentyMemberInfo> findByRoomId(Long roomId) {
        return repository.findByRoomId(roomId);
    }

    public TwentyMemberInfo findByRoomIdAndOrder(Long roomId, int order) {
        return repository.findByRoomIdAndOrder(roomId, order);
    }

    public boolean isRoomAllDead(Long roomId) {
        return repository.countByRoomIdAndIsAlive(roomId, true) == 0;
    }

    public TwentyMemberInfo save(TwentyMemberInfo twentyMemberInfo) {
        repository.save(twentyMemberInfo);
        return twentyMemberInfo;
    }

    public int updateIsAlive(Long userId, boolean isAlive) {
        return repository.updateIsAlive(userId, isAlive);
    }

    public int makeMemberAllAlive(Long roomId) {
        return updateIsAliveAll(roomId, true);
    }

    public int makeMemberNotAlive(Long UserId) {
        return updateIsAlive(UserId, false);
    }

    protected int updateIsAliveAll(Long roomId, boolean isAlive) {
        return repository.updateIsAliveAll(roomId, isAlive);
    }

    public int updateOrder(Long userId, int order) {
        return repository.updateOrder(userId, order);
    }

    public boolean deleteByUserId(Long userId) {
        return repository.delete(userId) == 1;
    }

    public int deleteByRoomId(Long roomId) {
        return repository.deleteByRoomId(roomId);
    }

}
