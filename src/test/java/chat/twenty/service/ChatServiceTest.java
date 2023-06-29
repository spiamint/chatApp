package chat.twenty.service;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class ChatServiceTest {

    @Autowired
    ChatgptService chatgptService;

    @Test
    public void sendMessage() {
        String responseMessage = chatgptService.sendMessage("안녕 gpt 야. 너의 api 를 사용하고있어.");
        System.out.print(responseMessage);
    }

    @Test
    public void askChatGpt() {
        String message = "반가워";
        List<MultiChatMessage> messageList = Arrays.asList(
                new MultiChatMessage("system", "반드시 2문장 이내로 대답해."),
                new MultiChatMessage("templates/user", message)
        );

        String gptResponse = chatgptService.multiChat(messageList);
//        log.info("gptResponse = {}", gptResponse);
        System.out.println("gptResponse = " + gptResponse);
    }


}
