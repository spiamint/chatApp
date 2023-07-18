package chat.twenty.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 스무고개 게임에서 RoomMember 의 상태를 관리
 * 게임 시작시 생성, 게임 종료시 삭제됨.
 */
@Getter @ToString @EqualsAndHashCode
public class TwentyMemberInfo {
    private Long userId;
    private Long roomId;
    private boolean isAlive;
    private int twentyOrder;

    public static TwentyMemberInfo createNewMemberInfo(Long userId, Long roomId, int order) {
        TwentyMemberInfo memberInfo = new TwentyMemberInfo();
        memberInfo.userId = userId;
        memberInfo.roomId = roomId;
        memberInfo.isAlive = true;
        memberInfo.twentyOrder = order;
        return memberInfo;
    }
}
