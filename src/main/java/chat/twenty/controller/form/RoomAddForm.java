package chat.twenty.controller.form;

import chat.twenty.enums.ChatRoomType;
import chat.twenty.enums.TwentyGameSubject;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RoomAddForm {
    @NotBlank
    @Size(max = 16)
    private String name;                // chatroom 이름
    private ChatRoomType roomType;          // chatroom 타입
    private TwentyGameSubject subject;  // 스무고개 주제
    @Size(max = 10)
    private String customSubject;       // 직접입력한 주제, 폼 기본값 ""

}
