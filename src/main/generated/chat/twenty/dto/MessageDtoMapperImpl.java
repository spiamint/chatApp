package chat.twenty.dto;

import chat.twenty.domain.ChatMessage;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-19T20:30:36+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.19 (Amazon.com Inc.)"
)
public class MessageDtoMapperImpl implements MessageDtoMapper {

    @Override
    public ChatMessage toMessage(ChatMessageDto chatMessageDto) {
        if ( chatMessageDto == null ) {
            return null;
        }

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent( chatMessageDto.getText() );
        chatMessage.setUsername( chatMessageDto.getFrom() );
        chatMessage.setType( chatMessageDto.getType() );
        chatMessage.setGptChat( chatMessageDto.isGptChat() );
        chatMessage.setGptUuid( chatMessageDto.getGptUuid() );

        return chatMessage;
    }

    @Override
    public ChatMessageDto toMessageDto(ChatMessage chatMessage) {
        if ( chatMessage == null ) {
            return null;
        }

        ChatMessageDto chatMessageDto = new ChatMessageDto();

        chatMessageDto.setText( chatMessage.getContent() );
        chatMessageDto.setFrom( chatMessage.getUsername() );
        if ( chatMessage.getCreatedAt() != null ) {
            chatMessageDto.setTime( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( chatMessage.getCreatedAt() ) );
        }
        chatMessageDto.setType( chatMessage.getType() );
        chatMessageDto.setGptChat( chatMessage.isGptChat() );
        chatMessageDto.setGptUuid( chatMessage.getGptUuid() );

        return chatMessageDto;
    }
}
