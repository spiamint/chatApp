package chat.twenty.enums;

public enum ChatRoomType {
    CHAT("채팅방"),       // 일반채팅
    TWENTY_GAME("스무고개방")     // 스무고개
    ;

    public String roomTypeName;

    ChatRoomType(String roomTypeName) {this.roomTypeName = roomTypeName;}

}
