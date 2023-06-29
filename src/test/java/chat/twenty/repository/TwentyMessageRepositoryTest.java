package chat.twenty.repository;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.TwentyMessage;
import chat.twenty.enums.ChatMessageType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
@Transactional
class TwentyMessageRepositoryTest {

    @Autowired
    TwentyMessageRepository twentyMessageRepository;
    List<TwentyMessage> testTwentyMessageList;

    @BeforeEach
    void init() {
        // roomId 1L 2L, userId 1L, 2L 은 사전에 DB 에 존재.
        testTwentyMessageList = List.of(
                new TwentyMessage(1L, 1L, ChatMessageType.CHAT, "testMessage r1 u1"),
                new TwentyMessage(1L, 2L, ChatMessageType.CHAT, "testMessage r1 u2"),
                new TwentyMessage(2L, 1L, ChatMessageType.CHAT, "testMessage r2 u1"),
                new TwentyMessage(2L, 2L, ChatMessageType.CHAT, "testMessage r2 u2")
        );
    }

    @Test
    void saveAndFindById() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        testTwentyMessageList.stream()
                .map(message -> twentyMessageRepository.save(message))
                // save() 로 리턴된 Stream<Message>
                .forEach(message -> {
                    Assertions.assertThat(twentyMessageRepository.findById(message.getId()))
                            .isEqualToIgnoringGivenFields(message, "createdAt");
                });
    }

    @Test
    void findAll() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        List<TwentyMessage> findChatMessageList = twentyMessageRepository.findAll();

        Assertions.assertThat(findChatMessageList).usingElementComparatorIgnoringFields("createdAt").containsAll(testTwentyMessageList);
    }

    @Test
    void findByRoomId() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        List<TwentyMessage> findChatMessageList = twentyMessageRepository.findByRoomId(1L);

        List<TwentyMessage> room1LChatMessageList = testTwentyMessageList.stream().filter(message -> message.getRoomId().equals(1L)).collect(Collectors.toList());
        Assertions.assertThat(findChatMessageList).usingElementComparatorIgnoringFields("createdAt").containsAll(room1LChatMessageList);
    }

    @Test
    void findByRoomIdAndUserId() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        List<TwentyMessage> findChatMessageList = twentyMessageRepository.findByRoomIdAndUserId(1L, 1L);

        List<TwentyMessage> room1LUser1LChatMessageList = testTwentyMessageList.stream().filter(message -> message.getRoomId().equals(1L) && message.getUserId().equals(1L)).collect(Collectors.toList());
        Assertions.assertThat(findChatMessageList).usingElementComparatorIgnoringFields("createdAt").containsAll(room1LUser1LChatMessageList);
    }

    @Test
    void update() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        TwentyMessage firstMessage = testTwentyMessageList.get(0);
        String updateContent = "updateMessage";

        // content 만 update 가 구현되어있음.
        TwentyMessage updateParam = TwentyMessage.builder().content(updateContent).build();

        twentyMessageRepository.update(firstMessage.getId(), updateParam);

        Assertions.assertThat(twentyMessageRepository.findById(firstMessage.getId()).getContent())
                .isEqualTo(updateContent);
    }

    @Test
    void deleteById() {
        testTwentyMessageList.forEach(message -> twentyMessageRepository.save(message));

        Long deleteMessageId = testTwentyMessageList.get(0).getId();
        twentyMessageRepository.deleteById(deleteMessageId);

        Assertions.assertThat(twentyMessageRepository.findById(deleteMessageId)).isNull();
    }
}