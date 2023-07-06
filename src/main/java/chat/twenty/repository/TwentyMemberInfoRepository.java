package chat.twenty.repository;

import chat.twenty.domain.TwentyMemberInfo;
import chat.twenty.mapper.TwentyMemberInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TwentyMemberInfoRepository {
    private final TwentyMemberInfoMapper mapper;

    public TwentyMemberInfo findById(Long userId) {
        return mapper.findById(userId);
    }

    public List<TwentyMemberInfo> findByRoomId(Long roomId) {
        return mapper.findByRoomId(roomId);
    }

    public TwentyMemberInfo findByRoomIdAndOrder(Long roomId, int order) {
        return mapper.findByRoomIdAndOrder(roomId, order);
    }

    public int save(TwentyMemberInfo twentyMemberInfo) {
        return mapper.save(twentyMemberInfo);
    }

    public int updateIsReady(Long userId, boolean isReady) {
        return mapper.updateIsReady(userId, isReady);
    }

    public int updateIsAlive(Long userId, boolean isAlive) {
        return mapper.updateIsAlive(userId, isAlive);
    }

    public int updateIsAliveAll(Long roomId, boolean isAlive) {
        return mapper.updateIsAliveAll(roomId, isAlive);
    }

    public int updateOrder(Long userId, int order) {
        return mapper.updateOrder(userId, order);
    }

    public int delete(Long userId) {
        return mapper.deleteById(userId);
    }

    public int deleteByRoomId(Long roomId) {
        return mapper.deleteByRoomId(roomId);
    }

}
