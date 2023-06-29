package chat.twenty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * RoomMember + User
 */
@Data
public class UserMemberDto {

    // from Member
    private Long roomId;
    private Long userId;
    @JsonProperty("isRoomConnected")
    private boolean isRoomConnected;
    @JsonProperty("isRoomOwner")
    private boolean isRoomOwner;
    @JsonProperty("isGptOwner")
    private boolean isGptOwner;
    @JsonProperty("isTwentyGameReady")
    private boolean isTwentyGameReady;

    // from User
    private Long id;
    private String username;

}
