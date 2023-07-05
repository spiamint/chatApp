package chat.twenty.service.gpt;

import chat.twenty.enums.TwentyGameSubject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
public class TwentyGameAnswer {
    public static String getRandomAnswer(TwentyGameSubject subject) {
        List<String> answerList = null;
        switch (subject) {
            case FOOD:
                answerList = foodAnswerList;
                break;
            default:
                answerList = null;
                log.warn("TwentyGameAnswer.getRandomAnswer, subject = {} is not supported", subject);
        }

        int answerIndex = new Random().nextInt(answerList.size());
        log.info("getRandomAnswer, answerIndex = {} answer = {}", answerIndex, answerList.get(answerIndex));
        return answerList.get(answerIndex);
    }

    private static final List<String> foodAnswerList = List.of(
            "라면", "비빔밥" , "떡볶이", "순대", "김밥", "초밥",
            "우동", "짜장면", "짬뽕", "탕수육", "볶음밥", "짬뽕밥", "짜장밥",
            "김치찌개", "된장찌개", "부대찌개", "순두부찌개", "김밥",
            "냉면", "비빔냉면", "물냉면", "칼국수", "군만두", "만두국",
            "떡국", "순대국밥", "감자탕", "해장국", "콩나물국밥", "순대국밥",
            "뼈해장국", "설렁탕", "갈비탕", "육개장", "매운탕", "갈비찜",
            "피자", "치킨", "햄버거", "돈까스", "찐만두", "치즈떡볶이",
            "돈까스", "치즈돈까스", "양송이스프", "옥수수스프", "까르보나라스파게티",
            "계란후라이", "계란말이", "계란찜", "삼겹살", "스테이크", "불고기", "제육볶음",
            "불닭볶음면", "짜파게티", "삼각김밥", "컵라면", "타코야키", "오무라이스", "오야꼬동",
            "마라탕", "전복죽", "팥죽", "육개장", "요구르트", "우유", "커피", "콜라", "사이다",
            "잔치국수", "송편", "떡꼬치", "닭발", "닭강정", "인절미", "해물파전", "감자전", "장조림",
            "아귀찜", "미역국", "곰탕", "사골국", "수제비", "꽃게탕", "냉모밀", "추어탕", "간장게장", "양념게장",
            "달고나", "뻥튀기", "붕어빵"
    );

}
