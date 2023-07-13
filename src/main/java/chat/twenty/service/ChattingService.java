package chat.twenty.service;

import chat.twenty.dto.ChatMessageDto;
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
        ChatMessageDto gptLeaveMessage = ChatMessageDto.createGptLeaveMessage(roomId);
        return gptLeaveMessage;
    }

    public ChatMessageDto chatToGpt(ChatMessageDto chatMessageDto) {
        ChatMessageDto resultChatMessageDto = gptService.sendGptChatRequest(chatMessageDto.getRoomId());

        if (resultChatMessageDto.getContent().length() > 200) {
            // 200자 이상이면 180자로 자르고, 마지막에 ...(너무 긴 대답) 을 붙인다.
            resultChatMessageDto.setContent(resultChatMessageDto.getContent().substring(0, 180) + "...(너무 긴 대답)");
        }

        return resultChatMessageDto;
    }

}