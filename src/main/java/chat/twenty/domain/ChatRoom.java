package chat.twenty.domain;

import chat.twenty.enums.ChatRoomType;
import chat.twenty.enums.TwentyGameSubject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoom {

    private Long id;                    // chatroom id
    private String name;                // chatroom 이름
    private ChatRoomType type;          // chatroom 타입
    private boolean isGptActivated;    // gpt 활성화 여부 DB default false
    private TwentyGameSubject subject;  // 스무고개 주제
    private String customSubject; // 스무고개 직접입력한 주제, 폼 기본값 ""
    private int twentyNext; // 스무고개 다음 순서

    public ChatRoom(String name) {
        this.name = name;
        this.isGptActivated = false;
    }

}
