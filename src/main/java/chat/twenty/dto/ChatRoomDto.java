package chat.twenty.dto;

import chat.twenty.domain.ChatRoom;
import chat.twenty.enums.ChatRoomType;
import chat.twenty.enums.TwentyGameSubject;
import lombok.Data;

@Data
public class ChatRoomDto {

    private Long id;                    // chatroom id
    private String name;                // chatroom 이름
    private ChatRoomType type;          // chatroom 타입
    private boolean isGptActivated;    // gpt 활성화 여부 DB default false
    private TwentyGameSubject subject;  // 스무고개 주제
    private String customSubject; // 스무고개 직접입력한 주제, 폼 기본값 ""
    private int twentyNext; // 스무고개 다음 순서

    // from count
    private int memberCount;
    private int connectedMemberCount;

    /**
     * ChatRoom -> ChatRoomDto with int connectedMemberCount
     */
    public static ChatRoomDto from(ChatRoom chatRoom, int connectedMemberCount) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.id = chatRoom.getId();
        chatRoomDto.name = chatRoom.getName();
        chatRoomDto.type = chatRoom.getType();
        chatRoomDto.isGptActivated = chatRoom.isGptActivated();
        chatRoomDto.subject = chatRoom.getSubject();
        chatRoomDto.customSubject = chatRoom.getCustomSubject();
        chatRoomDto.twentyNext = chatRoom.getTwentyNext();
        chatRoomDto.connectedMemberCount = connectedMemberCount;
        return chatRoomDto;
    }

}
