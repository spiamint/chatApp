package chat.twenty.enums;

public enum TwentyGameSubject {
    // 선 정의된 게임주제
    ANIMAL("동물"),
    PLANT("식물"),
    FOOD("음식"),
    JOB("직업"),
    SPORTS("스포츠"),
    COUNTRY("나라"),
    NONE("없음"),

    CUSTOM("직접입력");

    private String subjectName;

    TwentyGameSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
