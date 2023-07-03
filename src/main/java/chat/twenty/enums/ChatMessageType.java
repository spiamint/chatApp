package chat.twenty.enums;

/**
 * 메시지타입. 기본값 NONE, NULL 허용하지 않음.
 */
public enum ChatMessageType {
        // GLOBAL
        ENTER, CHAT, LEAVE, // 유저 입장, 채팅, 퇴장

        // GLOBAL GPT
        GPT_PROCESSING, GPT_ERROR,

        // CHAT
        DEACTIVATE_GPT, CHAT_FROM_GPT,    // 일반채팅, GPT 후처리 X
        ACTIVATE_GPT, CHAT_TO_GPT, // 일반채팅, GPT 후처리 필요
        GPT_ENTER, GPT_LEAVE,      // GPT 퇴장

        // TWENTY
        TWENTY_GAME_READY, TWENTY_GAME_UNREADY, TWENTY_GAME_END,  TWENTY_FROM_GPT, // 스무고개, GPT 후처리 x
        TWENTY_GAME_START, TWENTY_GAME_ASK, // 스무고개, GPT 후처리 필요
        TWENTY_GAME_ERROR,          // 스무고개 게임 에러

        // SYSTEM
        SYSTEM, NONE;        // 시스템


        /**
         * GPT 관련 후처리가 필요한지 여부 확인
         * TWENTY_GAME_START, TWENTY_GAME_ASK, TWENTY_GAME_ANSWER
         */
        public boolean needGptProcess() {
                return this == CHAT_TO_GPT || this == ACTIVATE_GPT ||
                        this == TWENTY_GAME_START || this == TWENTY_GAME_ASK;
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