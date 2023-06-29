package chat.twenty.service;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.RoomMember;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ChatMessageService 내부에 preProcessMessage() 가 존재했으나, 순환참조 문제로 인해
 * ChatMessageService 가 roomService, gptService 등에 의존하는것을 끊어내기 위해 만듦.
 *
 * 전처리 = 사용자에게 받은 메시지를 converAndSend 로 돌려주기 전의 처리
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagePreProcessor {

    private final ChatRoomService roomService;
    private final CustomGptService gptService;
    private final RoomMemberService memberService;

    // 메시지 전처리
    public boolean preProcessMessage(ChatMessage chatMessage, RoomMember member) {
        ChatRoom findRoom = null;
        Long userId = member.getUserId();
        Long roomId = member.getRoomId();
        boolean isSuccess = true;

        /*
        ENTER, CHAT, LEAVE,                                                 입장, 채팅, 퇴장
        ACTIVATE_GPT, DEACTIVATE_GPT, CHAT_TO_GPT, CHAT_FROM_GPT,           GPT 활성화, 비활성화, GPT 에게 채팅
        GPT_PROCESSING, GPT_OFFLINE,                                        GPT 처리중, GPT 오프라인
        SYSTEM, NONE                                                         시스템
         */
        switch (chatMessage.getType()) {
            case ENTER: break; // 입장
            case LEAVE: break; // 퇴장
            case CHAT: break; // 일반 채팅
            case CHAT_TO_GPT: break; // GPT 에게 채팅

            case ACTIVATE_GPT: // GPT 활성화
                // GPT 활성화 및 OWNER 설정
                Long gptOwnerId = gptService.activateGpt(roomId, userId);
                chatMessage.setGptUuid(memberService.findGptUuidByRoomId(roomId));
                findRoom = roomService.findById(roomId);
                log.info("GPT OWNER 설정 완료, roomId = {}, userId = {}, gptOwnerId = {}, gptActivated = {}", roomId, userId, gptOwnerId, findRoom.isGptActivated());
                break;

            case DEACTIVATE_GPT: // GPT 비활성화
                // GPT OWNER 해제
                RoomMember findMember = memberService.findById(roomId, userId);
                if (!findMember.isGptOwner()) {
                    // GPT OWNER 가 아니면 거부
                    log.info("GPT OWNER 가 아닙니다. roomId = {}, userId = {}", roomId, userId);
                    break;
                }
                Long wasGptOwnerId = gptService.deActivateGpt(roomId, userId);
                findRoom = roomService.findById(roomId);
                log.info("GPT OWNER 설정 완료, roomId = {}, userId = {}, wasGptOwnerId = {}, gptActivated = {}", roomId, userId, wasGptOwnerId, findRoom.isGptActivated());
                break;

            default:
                log.info("processChat, postProcess Message, defalut case. case(message.type) = {}", chatMessage.getType());
                isSuccess = false;
                break;
        }

        return isSuccess; // default case 일경우 false, 그 외 true
    }

}
