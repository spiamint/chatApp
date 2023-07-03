package chat.twenty.service.gpt;

import chat.twenty.domain.*;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.TwentyMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.enums.TwentyGameSubject;
import chat.twenty.service.lower.ChatMessageService;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import chat.twenty.service.lower.TwentyMessageService;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
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
    private final TwentyMessageService twentyMessageService;

    /**
     * Member 와 Chatroom 에서 gpt 활성화, 활성화된 gpt 의 UUID 반환
     */
    public String activateGpt(Long roomId, Long userId) {
        memberService.updateGptOwner(roomId, userId, true);
        String gptUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        memberService.updateGptUuid(roomId, userId, gptUuid);

        roomService.updateGptActivated(roomId, true);
        return gptUuid;
    }

    /**
     * Member 와 Chatroom 에서 gpt 비활성화, 성공여부 반환
     */
    public boolean deActivateGpt(Long roomId, Long userId) {
        boolean isSuccess = false;
        boolean isGptOwner = validateGptOwner(roomId, userId);
        if (isGptOwner) {
            memberService.updateGptOwner(roomId, userId, false);
            memberService.updateGptUuid(roomId, userId, null);
            roomService.updateGptActivated(roomId, false);
            isSuccess = true;
        }
        log.info("deActivateGpt, isGptOwner = {}, roomId = {}, userId = {}", isGptOwner, roomId, userId);
        return isSuccess;
    }

    protected boolean validateGptOwner(Long roomId, Long userId) {
        return memberService.findById(roomId, userId).isGptOwner();
    }

    /**
     * 외부사용 public
     * RoomMember, MessageType 을 이용하여 GPT 답변 메시지를 생성후 리턴
     */
    public ChatMessageDto sendGptChatRequest(Long roomId) {
        // gpt 식별자 획득
        String gptUuid = memberService.findGptUuidByRoomId(roomId);
        // 현재 gpt 와의 채팅목록 조회
        List<ChatMessage> chatMessageList = chatMessageService.findCurrentGptQueue(roomId, gptUuid);

        // 첫번째 메시지 프롬프트로 변경
        ChatMessage firstMessage = chatMessageList.get(0);
        setSystemPrompt(firstMessage, ChatMessageType.ACTIVATE_GPT, null);
        
        // gpt 요청 리스트 작성
        List<MultiChatMessage> gptRequestMessageList = makeGptRequestList(chatMessageList);
        
        // gpt 요청 후 응답
        String gptResponse = askMultiChatGpt(gptUuid, gptRequestMessageList);

        // GPT 답변으로 Message 객체 생성후 리턴
        return ChatMessageDto.createGptAnswerMessage(roomId, gptUuid, gptResponse);
    }

    /**
     * 스무고개 게임 진행중 GPT 에게 질문을 보냄.
     */
    public TwentyMessageDto sendGptTwentyRequest(Long roomId) {
        // gpt 식별자 획득
        String gptUuid = memberService.findGptUuidByRoomId(roomId);
        // 게임 주제 획득
        ChatRoom findRoom = roomService.findById(roomId);
        String subject = findRoom.getSubject() == TwentyGameSubject.CUSTOM ? findRoom.getSubject().getSubjectName() : findRoom.getCustomSubject();

        // roomId 와 gptUuid 를 기반으로, 현재 gpt 와의 채팅목록 조회
        List<TwentyMessage> twentyMessageList = twentyMessageService.findCurrentGptQueue(roomId, gptUuid);
        
        // 첫번째 메시지 프롬프트로 변경
        TwentyMessage firstMessage = twentyMessageList.get(0);
        setSystemPrompt(firstMessage, firstMessage.getType(), subject);

        // gpt 요청리스트 작성
        List<MultiChatMessage> gptRequestList = makeGptRequestList(twentyMessageList);

        // gpt 요청 후 응답
        String gptResponse = askMultiChatGpt(gptUuid, gptRequestList);

        // GPT 답변으로 TwentyMessage 객체 생성후 리턴
        return TwentyMessageDto.createGptAnswerMessage(roomId, gptUuid, gptResponse);
    }


    // protected =====================================================================================

    /**
     * GPT 요청 메시지 리스트의 system 프롬프트 작성
     * @param firstMessage : content 가 프롬프트로 replace 될 BaseMessage.
     * @param firstMessageType : TWENTY_GAME_START, ACTIVATE_GPT
     * @param replaceParam : 가변 프롬프트시, 대체할 문자열
     */
    protected void setSystemPrompt(BaseMessage firstMessage, ChatMessageType firstMessageType, String replaceParam) {

        firstMessage.setGptSystemRole();

        switch (firstMessageType) {
            case ACTIVATE_GPT:
                firstMessage.setGptPrompt(GptPrompt.CHAT_PROMPT.prompt);
                break;
            case TWENTY_GAME_START:
                firstMessage.setGptPrompt(GptPrompt.TWENTY_PROMPT.setSubject(replaceParam));
                break;
        }
    }

    /**
     * GPT 에 보낼 메시지 리스트를 라이브러리 스펙 List<MultiChatMessage>에 맞게 생성
     */
    protected List<MultiChatMessage> makeGptRequestList(List<? extends BaseMessage> messageList) {
        // MultiChatMessage 리스트 생성
        List<MultiChatMessage> requestMessageList = new ArrayList<>();

        messageList.stream().forEachOrdered(message ->
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
     *
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
        }catch (HttpServerErrorException.ServiceUnavailable e) {
            log.info("askMultiChatGpt ServiceUnavailable. e = {}, message = {}, gptUuid = {}", e, e.getMessage(), gptUuid);
            gptResponse = "GPT 서버가 현재 과부하상태(overloaded) 입니다. 잠시후 다시 시도해주세요";
        } catch (Exception e) {
            log.info("askMultiChatGpt Exception. e = {}, message = {}, gptUuid = {}", e, e.getMessage(), gptUuid);
            gptResponse = "Exception = " + e.getClass().getName() + "gptUuid = " + gptUuid;
        }
        log.info("askMultiChatGpt(), gptUuid = {}, gptResponse = {}", gptUuid, gptResponse);

        return gptResponse;
    }

    /**
     * 라이브러리를 사용하여, GPT 에게 직접 비동기 요청 및 비동기 응답
     * <p>
     * 라이브러리가 비동기로 작성되지 않았기 때문에, 해당 작업을 비동기화 하기위한 별도 메서드.
     * GPT 질의를 비동기화 하고, 답변인 gptResponse 를 Future<String> 으로 받기위해, AsyncResult<> 형태로 반환한다.
     * 뿐만아니라, GPT 의 대답에 timeout 도 설정한다.
     * <p>
     * 레퍼런스에, ListenableFuture 과 CompleteableFuture 등 논블록킹 방식도 있으나, 현재상황에서는 일단
     * 블로킹도 문제 없으니 해당 방식 사용.
     * <p>
     * 나중에 개선의 여지가 있음.
     */
    @Async
    protected Future<String> askMultiChatGptToAsync(List<MultiChatMessage> gptRequestMessageList) {
        String gptResponse = defaultGptService.multiChat(gptRequestMessageList);
        return new AsyncResult<>(gptResponse); // Future<> 의 구현체인 AsyncResult<> 반환
    }


}
