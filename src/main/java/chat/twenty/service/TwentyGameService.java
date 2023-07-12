package chat.twenty.service;

import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.RoomMember;
import chat.twenty.domain.TwentyMemberInfo;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.ChatRoomType;
import chat.twenty.enums.TwentyGameSubject;
import chat.twenty.exception.BanMemberNotValidException;
import chat.twenty.exception.TwentyGameAliveNotValidException;
import chat.twenty.exception.TwentyGameOrderNotValidException;
import chat.twenty.service.gpt.CustomGptService;
import chat.twenty.service.gpt.TwentyGameAnswer;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import chat.twenty.service.lower.TwentyMemberInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 스무고개 기능 관련  상위 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TwentyGameService {

    private final RoomMemberService memberService;
    private final ChatRoomService roomService;
    private final CustomGptService gptService;
    private final TwentyMemberInfoService memberInfoService;

    public boolean readyGame(Long roomId, Long userId) {
        return memberService.twentyReady(roomId, userId);
    }

    public boolean unreadyGame(Long roomId, Long userId) {
        return memberService.twentyUnready(roomId, userId);
    }

    // TODO try - catch 로 proceedGame 처럼.
    public boolean validateGameStart(Long roomId) {
        int memberCount = memberService.countRoomMember(roomId);
        int readyMemberCount = memberService.countTwentyReadyMemberByRoomId(roomId);
        log.info("validateGameStart() memberCount = {} readyMemberCount = {}", memberCount, readyMemberCount);
        return memberCount == readyMemberCount;
    }

    /**
     * 시작 검증후, 게임 시작
     * TWENTY_GAME_START
     */
    public TwentyMessageDto confirmGameStart(TwentyMessageDto twentyMessageDto) {

        // (개발용) PlayerInfo 초기화 *******************************************
        memberInfoService.deleteByRoomId(twentyMessageDto.getRoomId());

        ChatRoom findRoom = roomService.findById(twentyMessageDto.getRoomId());
        // 정답 생성
        if (findRoom.getSubject() != TwentyGameSubject.CUSTOM) {
            roomService.setTwentyAnswer(findRoom.getId(), TwentyGameAnswer.getRandomAnswer(findRoom.getSubject()));
        }
        // 순서결정용 배열 생성
        Integer[] orderArray = makeOrderArray(twentyMessageDto.getRoomId());
        // 방 멤버 조회
        List<RoomMember> readyMemberList = memberService.findIsTwentyReadyMemberByRoomId(twentyMessageDto.getRoomId());

        for (int i = 0; i < orderArray.length; i++) {
            // 방멤버에 따른 MemberInfo 생성 후 order 와 함께 저장
            RoomMember member = readyMemberList.get(i);
            memberInfoService.save(TwentyMemberInfo.createNewMemberInfo(member.getUserId(), member.getRoomId(), orderArray[i]));
        }

        // GPT 활성화 처리 및 uuid 획득
        String gptUuid = gptService.activateGpt(twentyMessageDto.getRoomId(), twentyMessageDto.getUserId());
        // 모든 플레이어 alive 처리
        memberInfoService.makeMemberAllAlive(twentyMessageDto.getRoomId());
        // memberInfo 모두 조회
        List<TwentyMemberInfo> twentyMemberInfoList = memberInfoService.findByRoomId(twentyMessageDto.getRoomId());

        twentyMessageDto.setMemberInfoList(twentyMemberInfoList); // MemberInfo 초기값 설정
        twentyMessageDto.setGptUuid(gptUuid);
        return twentyMessageDto;
    }

    /**
     * 스무고개 순서 배정용 배열 생성 및 반환
     * TWENTY_GAME_START
     */
    protected Integer[] makeOrderArray(Long roomId) {
        Integer[] orderArray = new Integer[memberService.countTwentyReadyMemberByRoomId(roomId)];
        for (int i = 0; i < orderArray.length; i++) {
            orderArray[i] = i;
        }

        List<Integer> orderList = Arrays.asList(orderArray);
        Collections.shuffle(orderList);

        orderArray = orderList.toArray(new Integer[orderList.size()]); // Object[] -> Integer[]
        return orderArray;
    }

    public TwentyMessageDto proceedStart(Long roomId) {
        resetOrder(roomId);

        TwentyMessageDto gptRespMessage = gptService.sendGptTwentyRequest(roomId);
        gptRespMessage.setNextUserId(memberInfoService.findByRoomIdAndOrder(roomId, 0).getUserId());

        return gptRespMessage;
    }

    /**
     * 스무고개 순서 초기화 후 다음순서(0) 을 반환
     */
    protected int resetOrder(long roomId) {
        roomService.resetTwentyOrder(roomId);
        return 0;
    }

    public TwentyMessageDto proceedGame(Long roomId, Long userId, int currentOrder) {
        validateOrder(roomId, userId, currentOrder); // 순서검증
        validateAlive(userId); // 플레이어 alive 검증

        // 순서 진행
        TwentyMemberInfo nextMemberInfo = proceedOrder(roomId, currentOrder);

        // GPT 질의
        TwentyMessageDto gptRespMessage = gptService.sendGptTwentyRequest(roomId);
        gptRespMessage.setNextUserId(nextMemberInfo.getUserId());

        log.info("proceedGame() userId = {}, nextOrder = {}", nextMemberInfo.getUserId(), nextMemberInfo.getTwentyOrder());
        return gptRespMessage;
    }

    protected boolean validateOrder(Long roomId, Long userId, int currentOrder) {
        if (roomService.findById(roomId).getTwentyNext() == currentOrder) {
            log.info("validateOrder() currentOrder is valid, currentOrder = " + currentOrder + " roomId = " + roomId);
            return true;
        } else {
            throw new TwentyGameOrderNotValidException("TwentyGameService.validateOrder() currentOrder is not valid," +
                    " currentOrder = " + currentOrder + " roomId = " + roomId, roomId, userId, currentOrder);
        }
    }

    /**
     * 플레이어 alive 상태 검증
     */
    protected boolean validateAlive(Long userId) {
        TwentyMemberInfo memberInfo = memberInfoService.findById(userId);
        log.info("validateAlive() userId = " + userId + "isAlive = " + memberInfo.isAlive());
        if (memberInfo.isAlive()) {
            return true;
        } else {
            throw new TwentyGameAliveNotValidException("TwentyGameService.validateAlive() not valid userId = "
                    + userId + "isAlive = " + memberInfo.isAlive(), userId, memberInfo.getRoomId());
        }
    }

    /**
     * 스무고개 순서 진행 후 다음 순서인 TwentyMemberInfo 반환
     */
    protected TwentyMemberInfo proceedOrder(long roomId, int currentOrder) {
        int playerCount = memberService.countTwentyReadyMemberByRoomId(roomId);
        int loopLimit = playerCount;
        int nextOrder = currentOrder;
        TwentyMemberInfo nextMemberInfo;

        do {
            nextOrder = ++nextOrder >= playerCount ? 0 : nextOrder; // 다음사람 순서
            nextMemberInfo = memberInfoService.findByRoomIdAndOrder(roomId, nextOrder); // 다음사람 조회
            loopLimit--; // playerCount-- 를 통해, 남은 playerCount 가 0이면 종료한다(보험).

        } while (!nextMemberInfo.isAlive() && loopLimit > 0); // 다음사람이 죽었으면 다시 다음사람 조회

        roomService.updateNextTwentyOrder(roomId, nextOrder);
        return nextMemberInfo;
    }

    protected boolean validateAnswer(String gptResponse) {
        return gptResponse.contains("#&#");
    }

    public TwentyMessageDto proceedAnswer(Long roomId, TwentyMessageDto twentyMessageDto) {
        // 게임 진행후 GPT 질의 까지는 동일
        TwentyMessageDto gptRespMessage = proceedGame(roomId, twentyMessageDto.getUserId(), twentyMessageDto.getOrder());
        // 정답인지 검증
        boolean isAnswer = validateAnswer(gptRespMessage.getContent());
        log.info("proceedAnswer() inputContent = {} gptRespMessage.getContent() = {}, isAnswer = {}", twentyMessageDto.getContent() , gptRespMessage.getContent(), isAnswer);

        if (isAnswer) {
            // 정답 처리
            finishGame(roomId);
            gptRespMessage.setType(ChatMessageType.TWENTY_GAME_END);
            gptRespMessage.setContent(gptRespMessage.getContent().replace("#&#", "")); // 정답 identifier 제거
            gptRespMessage.setTwentyWinner(twentyMessageDto.getUsername());
        } else {
            // 오답처리
            memberInfoService.makeMemberNotAlive(twentyMessageDto.getUserId());
            gptRespMessage.setTwentyDeadUserId(twentyMessageDto.getUserId());
            gptRespMessage.setContent(gptRespMessage.getContent() + " / " + twentyMessageDto.getUsername() + "님 사망");

            if (memberInfoService.isRoomAllDead(roomId)) {
                // 모든 유저가 정답을 맞히지 못함.
                gptRespMessage = proceedAbort(roomId, "모든 유저가 정답을 맞히지 못했습니다. 게임을 종료합니다.");
            }
        }

        return gptRespMessage;
    }

    /**
     * 게임에 문제가 생겨 즉시 종료할때 사용
     * @param message : 프론트로 전달할 채팅 메시지
     */
    public TwentyMessageDto proceedAbort(Long roomId, String message) {
        finishGame(roomId);
        return TwentyMessageDto.createAbortMessage(roomId, message);
    }

    protected void finishGame(long roomId) {
        resetOrder(roomId);

        memberInfoService.deleteByRoomId(roomId); // memberInfo 삭제

        memberService.findIsTwentyReadyMemberByRoomId(roomId).forEach(member -> {
            memberService.twentyUnreadyAllMember(roomId); // 모든유저 unready
            gptService.deActivateGpt(roomId, member.getUserId()); // gpt 비활성화
        });
    }

    public TwentyMessageDto banMember(Long roomId, Long userId, Long banUserId) {
        RoomMember findBanMember = memberService.findById(roomId, banUserId); // 강퇴당할 유저
        RoomMember findOwnerMember = memberService.findById(roomId, userId); // 강퇴할 유저 (owner)

        if (findOwnerMember == null || findBanMember == null
        || !findOwnerMember.isRoomOwner() || findOwnerMember.getUserId().equals(findBanMember.getUserId())) {
            throw new BanMemberNotValidException("강퇴 검증 오류: 강퇴할수 없습니다.", roomId, userId, banUserId, ChatRoomType.TWENTY_GAME);
        }

        memberService.leaveRoom(roomId, banUserId);
        return TwentyMessageDto.createBanMessage(roomId, banUserId, findBanMember.getUsername());
    }
}
