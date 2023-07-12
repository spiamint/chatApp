package chat.twenty.repository;

import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.RoomMember;
import chat.twenty.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
class RoomMemberRepositoryTest {
    @Autowired ChatRoomRepository roomRepository;
    @Autowired UserRepository userRepository;
    @Autowired RoomMemberRepository memberRepository;

    private ChatRoom testRoom;
    private User testUser;
    private RoomMember testMember;

    @BeforeEach
    void initTest() {
        log.info("initTest()");
        testUser = userRepository.save(new User("memoryTestUser"));
        testRoom = roomRepository.save(new ChatRoom("memoryTestRoom"));
        testMember = new RoomMember(testRoom.getId(), testUser.getId(), testUser.getUsername());
    }

    @Test
    void findRoomMember() {
        memberRepository.save(testMember);
        log.info("result = {}", memberRepository.findById(testRoom.getId(), testUser.getId()));
    }

    @Test
    void findAll() {
        memberRepository.findAll().forEach(roomMember ->
                log.info("result = {}", roomMember));
    }

    @Test
    void findMemberListByRoomId() {
        memberRepository.save(testMember);
        memberRepository.findMemberListByRoomId(testRoom.getId()).forEach(roomMember ->
                log.info("result = {}", roomMember)
        );
    }

    @Test
    void saveMember() {
        RoomMember savedMember = memberRepository.save(testMember);
        log.info("savedMember = {}", savedMember);
    }

    @Test
    void updateMember() {
        // 일단 패스
    }

    @Test
    void updateRoomConnected() {
        RoomMember savedMember = memberRepository.save(testMember);
        log.info("savedMember = {}", savedMember);
        memberRepository.updateIsRoomConnected(savedMember.getRoomId(), savedMember.getUserId(), false);
        log.info("updatedMember = {}", memberRepository.findById(savedMember.getRoomId(), savedMember.getUserId()));
    }

    @Test
    void updateRoomOwner() {
        RoomMember savedMember = memberRepository.save(testMember);
        log.info("savedMember = {}", savedMember);
        memberRepository.updateIsRoomOwner(savedMember.getRoomId(), savedMember.getUserId(), true);
        log.info("updatedMember = {}", memberRepository.findById(savedMember.getRoomId(), savedMember.getUserId()));
    }

    @Test
    void updateGptActivated() {
        RoomMember savedMember = memberRepository.save(testMember);
        log.info("savedMember = {}", savedMember);
        memberRepository.updateIsGptOwner(savedMember.getRoomId(), savedMember.getUserId(), true);
        log.info("updatedMember = {}", memberRepository.findById(savedMember.getRoomId(), savedMember.getUserId()));
    }

    @Test
    void deleteMember() {
        RoomMember savedMember = memberRepository.save(testMember);
        memberRepository.deleteById(savedMember.getRoomId(), savedMember.getUserId());
        Assertions.assertThat(memberRepository.findById(savedMember.getRoomId(), savedMember.getUserId())).isNull();
    }

}