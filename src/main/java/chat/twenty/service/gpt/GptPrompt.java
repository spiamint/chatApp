package chat.twenty.service.gpt;


/**
 * GPT 의 질의를 위한 prompt
 */
// 가변값을 enum 으로 사용하는것은 권장되지 않는다.
public enum GptPrompt {
    CHAT_PROMPT("Reply has to be shorter than 3 sentences." +
            "Also, Reply by korean. Say hello to users."),
    TWENTY_LEGACY_PROMPT("You are gonna be play 'Twenty questions game' with users by using korean." +
            "You have to think 'something about @' as 'answer of game', and users will ask you A number of questions to guess what you thought." +
            "If users couldn't guess correct 'answer of game', number of question can be grow until one of users guesses correct 'answer of game'." +
            "When one of users guesses correct 'answer of game'('Something about @' you last thought)" +
            ", your reply has to contain '#&#' identifier to determine winner." +
            "You must not tell the 'answer of game', whatever user's question was. " +
            "You have to reply by 'YES' or 'NO' to users" +
            "If you ready, declare the game-start by korean for example, " +
            "'지금부터 스무고게 게임을 시작합니다!' or " +
            "'스무고게 게임을 시작합니다. 질문해주세요'"),

    TWENTY_PROMPT("You are gonna be play 'Twenty questions game' with users by using korean." +
            "The subject is @, and The 'answer of game' is %. " +
            "Users will ask you A number of questions to guess 'answer of game(%)', you must reply by only 'Yes' or 'No'. to users." +
            "You must refuse reply when you can't start reply by 'Yes' or 'No' " +
            "Amount of questions can grow over 20." +
            "When one of users message has exact 'answer of game(%)', that user is winner," +
            "Your message must not contain the '%' until when someone win the game." +
            "If you ready, declare the game-start by korean. For example, " +
            "'지금부터 스무고게 게임을 시작합니다. 질문해주세요.' or " +
            "'스무고게 게임을 시작합니다!'");

    public String prompt;

    GptPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String setLegacyTwentyPrompt(String customSubject) {
        return this.prompt.replace("@", customSubject);
    }

    /**
     * 스무고개 게임에서, 프롬프트에 주제를 설정 후 프롬프트 반환
     */
    public String setTwentyPrompt(String subject, String answer) {
        // Apache Commons Lang 의 StringUtils 와 속도 거의 동일 (JDK 9 기준)
        return this.prompt.replace("%", answer).replace("@", subject);
    }

}
