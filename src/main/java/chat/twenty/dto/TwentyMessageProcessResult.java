package chat.twenty.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString @EqualsAndHashCode
public class TwentyMessageProcessResult {
    private final TwentyMessageDto twentyMessageDto;
    private boolean isSuccess;
    private boolean needResend;
    private boolean isTwentyStart;

    private boolean isStartValidateFailed;
    private Integer[] orderArray;

}
