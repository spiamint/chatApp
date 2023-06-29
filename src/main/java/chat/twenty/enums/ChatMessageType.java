package chat.twenty.enums;

/**
 * 메시지타입. 기본값 NONE, NULL 허용하지 않음.
 */
public enum ChatMessageType {
        ENTER, CHAT, LEAVE,         // 입장, 채팅, 퇴장
        ACTIVATE_GPT, DEACTIVATE_GPT, CHAT_TO_GPT, CHAT_FROM_GPT,    // GPT 활성화, 비활성화, GPT 에게 채팅
        TWENTY_GAME_READY, TWENTY_GAME_UNREADY, TWENTY_GAME_START, TWENTY_GAME_END,
        TWENTY_FROM_GPT, TWENTY_ANSWER_FROM_GPT, TWENTY_ANSWER_REQUEST, TWENTY_ANSWER_RESPONSE,
        TWENTY_GAME_ASK, TWENTY_GAME_ANSWER, // 스무고개 질문, 스무고개 정답
        GPT_ENTER, GPT_LEAVE, GPT_PROCESSING, GPT_OFFLINE,      // GPT 입장, 퇴장, 처리중, 오프라인
        TWENTY_GAME_ERROR,          // 스무고개 게임 에러
        SYSTEM, NONE;        // 시스템

        /**
         * 보여주기 위해 재전송 해야 하는 메시지인지 여부 확인
         * CHAT, CHAT_TO_GPT, TWENTY_GAME_ASK, TWENTY_GAME_ANSWER
         */
        public boolean needResend() {
                return this == CHAT || this == CHAT_TO_GPT ||
                        this == TWENTY_GAME_ASK || this == TWENTY_GAME_ANSWER;
        }

        /**
         * 재전송 해야 하는 시스템 메시지 여부 확인
         * TWENTY_GAME_START, TWENTY_GAME_END, TWENTY_GAME_READY, TWENTY_GAME_UNREADY
         */
        public boolean needResendSystem() {
                return this == TWENTY_GAME_START || this == TWENTY_GAME_END ||
                        this == TWENTY_GAME_READY || this == TWENTY_GAME_UNREADY;
        }
}

/**
 * 채팅 메시지 플로우
 * 일반채팅
 *      ENTER -> CHAT -> LEAVE
 *            -> ACTIVATE_GPT -> CHAT_TO_GPT -> (CHAT_FROM_GPT by gpt) -> DEACTIVATE_GPT ... -> LEAVE
 *            -> ACTIVATE_GPT -> CHAT_TO_GPT -> (CHAT_FROM_GPT by gpt) -> CHAT -> ... -> DEACTIVATE_GPT -> CHAT ... -> LEAVE
 *
 * 스무고개
 *      ENTER -> CHAT -> LEAVE
 *            -> TWENTY_GAME_READY -> (TWENTY_GAME_START by owner) -
 *              > TWENTY_GAME_ASK -> ... -> TWENTY_GAME_ANSWER -> (TWENTY_GAME_END by system) -
 *              > TWENTY_GAME_READY -> ... -> LEAVE
 *            -> TWENTY_GAME_READY -> (TWENTY_GAME_START by owner) -
 *              > CHAT -> CHAT OR TWENTY_GAME_ASK ... -> TWENTY_GAME_ANSWER -> (TWENTY_GAME_END by system) -
 *              > TWENTY_GAME_READY -> ... -> LEAVE
 *
 */