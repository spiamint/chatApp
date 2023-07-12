package chat.twenty.dto;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.TwentyMessage;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-11T23:02:51+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.19 (Amazon.com Inc.)"
)
public class MessageDtoMapperImpl implements MessageDtoMapper {

    @Override
    public TwentyMessage toTwentyMessage(TwentyMessageDto twentyMessageDto) {
        if ( twentyMessageDto == null ) {
            return null;
        }

        TwentyMessage.TwentyMessageBuilder<?, ?> twentyMessage = TwentyMessage.builder();

        twentyMessage.roomId( twentyMessageDto.getRoomId() );
        twentyMessage.userId( twentyMessageDto.getUserId() );
        twentyMessage.username( twentyMessageDto.getUsername() );
        twentyMessage.type( twentyMessageDto.getType() );
        twentyMessage.content( twentyMessageDto.getContent() );
        twentyMessage.createdAt( twentyMessageDto.getCreatedAt() );
        twentyMessage.isGptChat( twentyMessageDto.isGptChat() );
        twentyMessage.gptUuid( twentyMessageDto.getGptUuid() );

        return twentyMessage.build();
    }

    @Override
    public TwentyMessageDto toTwentyMessageDto(TwentyMessage twentyMessage) {
        if ( twentyMessage == null ) {
            return null;
        }

        TwentyMessageDto twentyMessageDto = new TwentyMessageDto();

        twentyMessageDto.setRoomId( twentyMessage.getRoomId() );
        twentyMessageDto.setUserId( twentyMessage.getUserId() );
        twentyMessageDto.setType( twentyMessage.getType() );
        twentyMessageDto.setUsername( twentyMessage.getUsername() );
        twentyMessageDto.setContent( twentyMessage.getContent() );
        twentyMessageDto.setCreatedAt( twentyMessage.getCreatedAt() );
        twentyMessageDto.setGptChat( twentyMessage.isGptChat() );
        twentyMessageDto.setGptUuid( twentyMessage.getGptUuid() );

        return twentyMessageDto;
    }

    @Override
    public ChatMessage toChatMessage(ChatMessageDto chatMessageDto) {
        if ( chatMessageDto == null ) {
            return null;
        }

        ChatMessage.ChatMessageBuilder<?, ?> chatMessage = ChatMessage.builder();

        chatMessage.roomId( chatMessageDto.getRoomId() );
        chatMessage.userId( chatMessageDto.getUserId() );
        chatMessage.username( chatMessageDto.getUsername() );
        chatMessage.type( chatMessageDto.getType() );
        chatMessage.content( chatMessageDto.getContent() );
        chatMessage.createdAt( chatMessageDto.getCreatedAt() );
        chatMessage.isGptChat( chatMessageDto.isGptChat() );
        chatMessage.gptUuid( chatMessageDto.getGptUuid() );

        return chatMessage.build();
    }

    @Override
    public ChatMessageDto toChatMessageDto(ChatMessage chatMessage) {
        if ( chatMessage == null ) {
            return null;
        }

        ChatMessageDto chatMessageDto = new ChatMessageDto();

        chatMessageDto.setRoomId( chatMessage.getRoomId() );
        chatMessageDto.setUserId( chatMessage.getUserId() );
        chatMessageDto.setType( chatMessage.getType() );
        chatMessageDto.setUsername( chatMessage.getUsername() );
        chatMessageDto.setContent( chatMessage.getContent() );
        chatMessageDto.setCreatedAt( chatMessage.getCreatedAt() );
        chatMessageDto.setGptChat( chatMessage.isGptChat() );
        chatMessageDto.setGptUuid( chatMessage.getGptUuid() );

        return chatMessageDto;
    }
}
