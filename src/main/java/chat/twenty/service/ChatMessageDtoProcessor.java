package chat.twenty.service;

import chat.twenty.domain.ChatMessage;
import chat.twenty.dto.ChatMessageDto;
import chat.twenty.dto.MessageDtoMapper;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.lower.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ChatMessageService 내부에 preProcessMessage() 가 존재했으나, 순환참조 문제로 인해
 * ChatMessageService 가 roomService, gptService 등에 의존하는것을 끊어내기 위해 만듦.
 *
 * 전처리 = 사용자에게 받은 메시지를 converAndSend 로 돌려주기 전의 처리
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatMessageDtoProcessor {

    private final ChattingService chattingService;
    private final ChatMessageService messageService;

    // 메시지 전처리
    public ChatMessageDto processMessage(ChatMessageDto chatMessageDto) {
        Long userId = chatMessageDto.getUserId();
        Long roomId = chatMessageDto.getRoomId();

        ChatMessageType messageType = chatMessageDto.getType();
        log.info("START processMessage() ChatMessageDto = {}", chatMessageDto);

        switch (messageType) {
            case ACTIVATE_GPT: // GPT 활성화
                String gptUuid = chattingService.activateGpt(roomId, userId);
                chatMessageDto.setGptUuid(gptUuid);
                break;
            case DEACTIVATE_GPT: // GPT 비활성화
                chatMessageDto = chattingService.deActivateGpt(chatMessageDto);
                break;
            default:
                log.info("processMessage, default case. case(message.type) = {}", chatMessageDto.getType());
                break;
        }

        // DB 저장 전 시간작성
        chatMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        // DB 에 메시지 저장
        ChatMessage chatMessage = MessageDtoMapper.INSTANCE.toChatMessage(chatMessageDto);
        messageService.saveMessage(chatMessage);

        log.info("FINISH processMessage() ChatMessageDto = {}", chatMessageDto);

        return chatMessageDto; // default case 일경우 false, 그 외 true
    }

    public ChatMessageDto processGpt(ChatMessageDto chatMessageDto) {
        ChatMessageDto resultMessageDto = null;
        ChatMessageType messageType = chatMessageDto.getType();
        log.info("START processGPT() ChatMessageDto = {}", chatMessageDto);

        switch (messageType) {
            case ACTIVATE_GPT:
                resultMessageDto = chattingService.chatToGpt(chatMessageDto);
                resultMessageDto.setType(ChatMessageType.GPT_ENTER);
                break;
            case CHAT_TO_GPT:
                resultMessageDto = chattingService.chatToGpt(chatMessageDto);
                break;
            default:
                log.info("ChatMessage processGpt default case. case(message.type) = {}", messageType);
                break;
        }

        // DB 저장 전 시간작성
        resultMessageDto.setCreatedAt(LocalDateTime.now().withNano(0));

        // DB 에 메시지 저장
        ChatMessage chatMessage = MessageDtoMapper.INSTANCE.toChatMessage(resultMessageDto);
        messageService.saveMessage(chatMessage);

        log.info("FINISH processGpt() ChatMessageDto = {}", chatMessageDto);
        return resultMessageDto;
    }


}
