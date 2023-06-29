package chat.twenty.service;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.RoomMember;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.exception.TwentyGameOrderNotValidException;
import chat.twenty.service.lower.ChatMessageService;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 다른 서비스를 이용하는 상위 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TwentyGameService {

    private final RoomMemberService memberService;
    private final ChatRoomService roomService;
    private final ChatMessageService messageService;
    private final CustomGptService gptService;

    public boolean readyGame(Long roomId, Long userId) {
        return memberService.twentyReady(roomId, userId);
    }

    public boolean unreadyGame(Long roomId, Long userId) {
        return memberService.twentyUnready(roomId, userId);
    }

    // TODO try - catch 로 proceedGame 처럼.
    public boolean validateGameStart(Long roomId) {
        int memberCount = memberService.countMemberByRoomId(roomId);
        int readyMemberCount = memberService.countTwentyReadyMemberByRoomId(roomId);
        log.info("validateGameStart() memberCount = {} readyMemberCount = {}", memberCount, readyMemberCount);
        return memberCount == readyMemberCount;
    }

    public Map<String, Object> proceedGame(int twentyOrder, RoomMember member, ChatMessage chatMessage) {
        ChatMessage gptResponse;

        if (twentyOrder == -1) {
            // TWENTY_GAME_START 일때 twentyOrder == -1, 기존 order 초기화 및 검증 + 진행생략
            resetOrder(member.getRoomId());
            gptResponse = gptService.sendGptTwentyRequest(chatMessage.getType(), member);
            return Map.of("gptResponse", gptResponse,
                    "nextOrder", 0);
        }

        // 순서 검증
        roomService.findById(member.getRoomId()).getTwentyNext();
        validateOrder(member, twentyOrder);

        // GPT 질의
        gptResponse = gptService.sendGptTwentyRequest(chatMessage.getType(), member);
        
        // 순서 진행 (DB 질의 줄일수 있음)
        int playerCount = memberService.countTwentyReadyMemberByRoomId(member.getRoomId());
        int nextOrder = proceedToNextOrder(member.getRoomId());
        nextOrder = nextOrder < playerCount ? nextOrder : resetOrder(member.getRoomId());
        log.info("proceedGame() nextOrder = {}", nextOrder);

        // 정답 검증 및 정답처리
        if (validateAnswer(gptResponse.getContent())) {
            log.info("proceedGame() validate Answer true, roomId = {}, userId = {}, content = {}",
                    member.getRoomId(), member.getUserId(), gptResponse.getContent());
            gptResponse.setType(ChatMessageType.TWENTY_GAME_END);
            finishGame(member.getRoomId());
        };

        return Map.of("gptResponse", gptResponse,
                "nextOrder", nextOrder);
    }

    protected boolean validateOrder(RoomMember member, int order) {
        Long roomId = member.getRoomId();
        Long userId = member.getUserId();
        if (roomService.findById(roomId).getTwentyNext() == order) {
            log.info("validateOrder() order is valid, order = " + order + " roomId = " + roomId + " userId = " + userId);
            return true;
        } else {
            throw new TwentyGameOrderNotValidException("TwentyGameService.validateOrder() order is not valid, order = " + order + " roomId = " + roomId, roomId, userId);
        }
    }

    /**
     * 스무고개 순서 진행 후 다음 순서를 반환
     */
    protected int proceedToNextOrder(long roomId) {
        roomService.proceedNextTwentyOrder(roomId);
        return roomService.findById(roomId).getTwentyNext();
    }

    /**
     * 스무고개 순서 초기화 후 다음순서(0) 을 반환
     */
    protected int resetOrder(long roomId) {
        roomService.resetTwentyOrder(roomId);
        return 0;
    }

    public boolean validateAnswer(String gptResponse) {
        return gptResponse.contains("##");
    }

    public void finishGame(long roomId) {
        memberService.findIsTwentyReadyMemberByRoomId(roomId).forEach(member -> {
            memberService.twentyUnready(roomId, member.getUserId()); // 모든 유저 ready 해제
            roomService.updateGptActivated(roomId, false); // gptActivated 초기화
            memberService.updateGptUuid(roomId, member.getUserId(), null); // gptUuid 초기화
        });
    }
}
