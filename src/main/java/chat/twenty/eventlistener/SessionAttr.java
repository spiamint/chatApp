package chat.twenty.eventlistener;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * StompSessionAttributes 맵핑용 DTO
 */
@Builder
@Getter @ToString @EqualsAndHashCode
public class SessionAttr {

    private Long roomId;
    private Long userId;
    private String username;
    private String subUrl;
    private String gptUuid;     // nullable, gpt 가 activate 되야 생성됨.
}
