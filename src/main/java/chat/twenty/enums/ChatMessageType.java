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
        TWENTY_GAME_START, TWENTY_GAME_ASK, TWENTY_GAME_ANSWER, // 스무고개, GPT 후처리 필요
        TWENTY_GAME_ERROR, LEAVE_WHILE_PLAYING,         // 스무고개 게임 에러, 게임중 퇴장

        // SYSTEM
        SYSTEM, NONE, ROOM_DELETED       // 시스템
        ;


        /**
         * GPT 관련 후처리가 필요한지 여부 확인
         * TWENTY_GAME_START, TWENTY_GAME_ASK, TWENTY_GAME_ANSWER
         */
        public boolean needGptProcess() {
                return this == CHAT_TO_GPT || this == ACTIVATE_GPT ||
                        this == TWENTY_GAME_START || this == TWENTY_GAME_ASK || this == TWENTY_GAME_ANSWER;
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
 * 
 * TWENTY_GAME_ANSWER 사용이유
 *      user 가 마구잡이로 정답은 XX 야 라고 여러번 보내버리면, 생성형 AI 의 특성인지 모르겟지만
 *      지맘대로 정답으로 인정해버림. 따라서 ANSWER 횟수를 제한하기위해 사용
 *
 * TWENTY_GAME_SKIP 사용이유
 *      alive = false 유저가 있을때, 순서가 꼬이게 되는데 이를 서버에서 일일히 보정하기 어려워서
 *      그냥 클라이언트에서 자동으로 메시지를 보내게 하고, validateAlive 에서 거른뒤 SKIP 메시지 보내기로함.
 *      이러면 순서에 영향X
 */