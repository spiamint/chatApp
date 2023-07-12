package chat.twenty.enums;

public enum TwentyGameSubject {
    // 선 정의된 게임주제

    FOOD("음식"),
    STARCRAFT("게임 '스타크래프트' 의 유닛"),


    ANIMAL("동물"),
    PLANT("식물"),
    JOB("직업"),
    SPORTS("스포츠"),
    COUNTRY("나라"),
    NONE("없음"),

    CUSTOM("커스텀주제");

    private String subjectName;

    TwentyGameSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
