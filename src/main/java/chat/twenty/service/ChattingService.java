package chat.twenty.service;

import chat.twenty.dto.ChatMessageDto;
import chat.twenty.enums.ChatMessageType;
import chat.twenty.service.gpt.CustomGptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 일반 채팅 기능 상위 서비스
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {
    private final CustomGptService gptService;

    public String activateGpt(Long roomId, Long userId) {
        return gptService.activateGpt(roomId, userId);
    }

    public ChatMessageDto deActivateGpt(ChatMessageDto chatMessageDto) {
        Long roomId = chatMessageDto.getRoomId();
        Long userId = chatMessageDto.getUserId();

        gptService.deActivateGpt(roomId, userId);
        chatMessageDto.setType(ChatMessageType.GPT_LEAVE);
        chatMessageDto.setContent("GPT 가 비활성화 되었습니다.");
        return chatMessageDto;
    }

    public ChatMessageDto chatToGpt(ChatMessageDto chatMessageDto) {
        ChatMessageDto resultChatMessageDto = gptService.sendGptChatRequest(chatMessageDto.getRoomId());
        return resultChatMessageDto;
    }

}