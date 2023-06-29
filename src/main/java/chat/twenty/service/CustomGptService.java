package chat.twenty.service;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.RoomMember;
import chat.twenty.domain.UserType;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.TwentyGameSubject;
import chat.twenty.service.lower.ChatMessageService;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * GPT 관련 처리를 하는 서비스, 라이브러리의 DefaultGptService 를 사용한다.
 * 라이브러리의 DefaultGptService 를 의존하는 유일한 객체일것.
 * <p>
 * 다른 서비스를 참조하는 상위서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomGptService {
    private final ChatgptService defaultGptService; // 라이브러리로 부터 DefaultChatGptService 를 주입받아 사용
    private final RoomMemberService memberService;
    private final ChatRoomService roomService;
    private final ChatMessageService chatMessageService;

    /**
     * Member 와 Chatroom 에서 gpt 활성화, userId 반환
     */
    public Long activateGpt(Long roomId, Long userId) {
        memberService.updateGptOwner(roomId, userId, true);
        String gptUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        memberService.updateGptUuid(roomId, userId, gptUuid);

        roomService.updateGptActivated(roomId, true);
        return userId;
    }

    /**
     * Member 와 Chatroom 에서 gpt 비활성화, userId 반환
     */
    public Long deActivateGpt(Long roomId, Long userId) {
        memberService.updateGptOwner(roomId, userId, false);
        memberService.updateGptUuid(roomId, userId, null);

        roomService.updateGptActivated(roomId, false);
        return userId;
    }

    /**
     * 외부사용 public
     * RoomMember, MessageType 을 이용하여 GPT 답변 메시지를 생성후 리턴
     */
    public ChatMessage sendGptRequest(ChatMessageType requestChatMessageType, RoomMember member) {

        ChatMessageType gptAnswerType = null; // Gpt 답변타입
        String gptUuid = ""; // 현재 GPT 식별자
        String gptResponse = ""; // GPT 답변
        Long roomId = member.getRoomId();
        List<MultiChatMessage> gptRequestMessageList; // Gpt 요청 메시지 리스트

        // requestMessageType 따라 gptAnswerType 결정 및 Gpt 질의
        switch (requestChatMessageType) {
            case ACTIVATE_GPT:
                gptAnswerType = ChatMessageType.GPT_ENTER;

                gptUuid = memberService.findGptUuidByRoomId(roomId); // gpt 식별자
                gptRequestMessageList = makeGptMessageList(roomId, gptUuid); // 요청 리스트
                gptResponse = askMultiChatGpt(gptUuid, gptRequestMessageList);
                break;
            case CHAT_TO_GPT:
                gptAnswerType = ChatMessageType.CHAT_FROM_GPT;

                gptUuid = memberService.findGptUuidByRoomId(roomId);
                gptRequestMessageList = makeGptMessageList(roomId, gptUuid);
                gptResponse = askMultiChatGpt(gptUuid, gptRequestMessageList);
                break;
            case DEACTIVATE_GPT:
                gptAnswerType = ChatMessageType.GPT_LEAVE;

                // GPT 질의 없이 답변생성
                gptResponse = "GPT 가 비활성화 되었습니다.";
                break;
            default:
                log.info("sendGptRequest, default case. message.type = {}", requestChatMessageType);
                gptResponse = "GPT request error";
                break;
        }

        // GPT 답변으로 Message 객체 생성후 리턴
        return new ChatMessage(roomId, UserType.GPT.getId(), gptAnswerType,
                UserType.GPT.getUsername(), gptResponse, true, gptUuid);
    }

    public ChatMessage sendGptTwentyRequest(ChatMessageType requestChatMessageType, RoomMember member) {

        ChatMessageType gptAnswerType = null; // Gpt 답변타입
        String gptUuid = ""; // 현재 GPT 식별자
        String gptResponse = ""; // GPT 답변
        Long roomId = member.getRoomId();
        List<MultiChatMessage> gptRequestMessageList; // Gpt 요청 메시지 리스트

        // requestMessageType 따라 gptAnswerType 결정 및 Gpt 질의
        switch (requestChatMessageType) {
            case TWENTY_GAME_START:
                gptAnswerType = ChatMessageType.TWENTY_FROM_GPT;

                gptUuid = memberService.findGptUuidByRoomId(roomId); // gpt 식별자
                gptRequestMessageList = makeGptMessageList(roomId, gptUuid); // 요청 리스트
                gptResponse = askMultiChatGpt(gptUuid, gptRequestMessageList);

                break;
            case TWENTY_GAME_ASK:
                gptAnswerType = ChatMessageType.TWENTY_FROM_GPT;

                gptUuid = memberService.findGptUuidByRoomId(roomId); // gpt 식별자
                gptRequestMessageList = makeGptMessageList(roomId, gptUuid); // 요청 리스트
                gptResponse = askMultiChatGpt(gptUuid, gptRequestMessageList);
                break;
            case TWENTY_GAME_ANSWER:
                break;
            case TWENTY_GAME_END:
                break;
            default:
                log.info("sendGptTwentyRequest, default case. message.type = {}", requestChatMessageType);
                gptResponse = "GPT request error";
                break;
        }

        // GPT 답변으로 Message 객체 생성후 리턴
        return new ChatMessage(roomId, UserType.GPT.getId(), gptAnswerType,
                UserType.GPT.getUsername(), gptResponse, true, gptUuid);
    }

    // protected ======================

    /**
     * GPT 에 보낼 메시지 리스트를 라이브러리 스펙 List<MultiChatMessage>에 맞게 생성
     */
    protected List<MultiChatMessage> makeGptMessageList(Long roomId, String gptUuid) {
        List<ChatMessage> gptChatMessageList = chatMessageService.findCurrentGptQueue(roomId, gptUuid);

        // ACTIVATE 메시지 처리
        Optional<ChatMessage> activateMessage = gptChatMessageList.stream()
                .filter(chatMessage -> chatMessage.getType() == ChatMessageType.ACTIVATE_GPT).findFirst();
        if (activateMessage.isPresent()) modifyActivateMessage(activateMessage.get());

        // DEACTIVATE 메시지 처리 (질의 안함으로 수정)
        /*
        Message deactivateMessage = gptMessageList.stream()
                .filter(message -> message.getType().equals(MessageType.DEACTIVATE_GPT))
                .findAny().orElse(null);
        if (deactivateMessage != null) {
            // 내용을 종료 메시지로 변경
            deactivateMessage.setContent("고마워. 접속종료.");
        }
         */

        // TWENTY_GAME_START 메시지 처리
        Optional<ChatMessage> twentyGameStartMessage = gptChatMessageList.stream()
                .filter(chatMessage -> chatMessage.getType() == ChatMessageType.TWENTY_GAME_START).findFirst();
        if (twentyGameStartMessage.isPresent()) {
            modifyTwentyGameStartMessage(twentyGameStartMessage.get(), roomId); // TWENTY_GAME_START 메시지 변경
        }

        // MultiChatMessage 리스트 생성
        List<MultiChatMessage> requestMessageList = new ArrayList<>();
        gptChatMessageList.stream()
                .forEachOrdered(message ->
                        requestMessageList.add(new MultiChatMessage(
                                // role = "system" or "assistant" or "user"
                                determineRole(message.getUserId()),
                                // content = "username: content" or "content" when assistant
                                (message.getUsername().equals("assistant") ? "" : message.getUsername() + ": ") + message.getContent())));

        // 전체 리스트 로그출력
        log.info("raw gptRequestList in makeGptMessageList()\n{}", requestMessageList.stream()
                .map(MultiChatMessage::getContent)
                .collect(Collectors.joining("\n")));

        return requestMessageList;
    }

    /**
     * ACTIVATE_GPT 메시지 수정
     */
    protected void modifyActivateMessage(ChatMessage activateChatMessage) {
        // user_id 를 system 으로 변경(role 을 system 으로 변경하기 위해). 내용을 초기화 메시지로 변경
        activateChatMessage.setUserId(UserType.SYSTEM.getId());
        activateChatMessage.setUsername(UserType.SYSTEM.getUsername());
        activateChatMessage.setContent("answer should be shorter than 3 sentences; 안녕하세요? 저는" + activateChatMessage.getUsername() + " 입니다.");
    }

    /**
     * TWENTY_GAME_START 메시지 수정
     * 주제설정을 위한 room 조회에 roomId 필요
     */
    protected void modifyTwentyGameStartMessage(ChatMessage twentyGameStartMessage, Long roomId) {

        ChatRoom findRoom = roomService.findById(roomId);
        String subject = findRoom.getSubject() == TwentyGameSubject.CUSTOM ? findRoom.getCustomSubject() : findRoom.getSubject().toString();
        log.info("modifyTwentyGameStartMessage, subject = {}", subject);
        // user_id 를 system 으로 변경(role 을 system 으로 변경하기 위해). 내용을 초기화 메시지로 변경
        twentyGameStartMessage.setUserId(UserType.SYSTEM.getId());
        twentyGameStartMessage.setUsername(UserType.SYSTEM.getUsername());

        String twentyStartPrompt = "You should play 'twenty questions game' with users." +
                "You should think something about '" + subject +"' and users will ask you 20 questions to guess what you are thinking." +
                "If one of user can guess what you thought, your next answer should contain '##' identifier to determine winner." +
                "You should not tell the answer of twenty game, whatever user's question is. " +
                "You can Only answer by 'YES' or 'NO' to users, and you should send as korean language." +
                "준비가 되었으면 '스무고개 게임을 시작합니다' 라고 보내주세요.";

        twentyGameStartMessage.setContent(twentyStartPrompt);
    }

    /**
     * message.userId 에 따라 gpt 에게 보낼 role 을 결정한다.
     * "system", "assistant", "user" 중 하나를 반환
     */
    protected String determineRole(long id) {
        if (id == UserType.GPT.getId()) {
            return UserType.GPT.getUsername(); // assistant
        } else if (id == UserType.SYSTEM.getId()) {
            return UserType.SYSTEM.getUsername(); // system
        } else {
            return "user";
        }
    }

    /**
     * GPT 에게 질문 요청
     * @param gptUuid               : gpt 식별자 (나중에 roomId 도 같이 넣을지 고려)
     * @param gptRequestMessageList : 요청 MultiChatMessage 리스트
     * @return
     */
    @Async
    protected String askMultiChatGpt(String gptUuid, List<MultiChatMessage> gptRequestMessageList) {
//        log.info("askMultiChatGpt() gptRequestMessageList = {}", gptRequestMessageList);
        String gptResponse = ""; // GPT 답변

        try {
            Future<String> gptResponseFuture = askMultiChatGptToAsync(gptRequestMessageList);
            gptResponse = gptResponseFuture.get(30, TimeUnit.SECONDS); // 30초 타임아웃
        } catch (TimeoutException e) {
            log.info("askMultiChatGpt TimeoutException. e = {}, message = {}, gptUuid = {}", e, e.getMessage(), gptUuid);
            gptResponse = "GPT 응답시간이 초과되었습니다. 다시 시도해주세요";
        } catch (Exception e) {
            log.info("askMultiChatGpt Exception. e = {}, message = {}, gptUuid = {}", e, e.getMessage(), gptUuid);
            gptResponse = "Exception = " + e.getClass().getName() + "gptUuid = " + gptUuid;
        }
        log.info("askMultiChatGpt(), gptUuid = {}, gptResponse = {}", gptUuid, gptResponse);

        return gptResponse;
    }

    /**
     * 라이브러리를 사용하여, GPT 에게 직접 비동기 요청 및 비동기 응답
     * 
     * 라이브러리가 비동기로 작성되지 않았기 때문에, 해당 작업을 비동기화 하기위한 별도 메서드.
     * GPT 질의를 비동기화 하고, 답변인 gptResponse 를 Future<String> 으로 받기위해, AsyncResult<> 형태로 반환한다.
     * 뿐만아니라, GPT 의 대답에 timeout 도 설정한다.
     *
     * 레퍼런스에, ListenableFuture 과 CompleteableFuture 등 논블록킹 방식도 있으나, 현재상황에서는 일단
     * 블로킹도 문제 없으니 해당 방식 사용.
     *
     * 나중에 개선의 여지가 있음.
     */
    @Async
    protected Future<String> askMultiChatGptToAsync(List<MultiChatMessage> gptRequestMessageList) {
        String gptResponse = defaultGptService.multiChat(gptRequestMessageList);
        return new AsyncResult<>(gptResponse); // Future<> 의 구현체인 AsyncResult<> 반환
    }


}
