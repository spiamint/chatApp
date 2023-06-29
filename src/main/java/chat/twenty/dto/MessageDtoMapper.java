package chat.twenty.dto;

import chat.twenty.domain.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageDtoMapper {
    MessageDtoMapper INSTANCE = Mappers.getMapper(MessageDtoMapper.class);
    @Mapping(target = "content", source = "text")
    @Mapping(target = "username", source = "from")
    ChatMessage toMessage(ChatMessageDto chatMessageDto);

    @Mapping(target = "text", source = "content")
    @Mapping(target = "from", source = "username")
    @Mapping(target = "time", source = "createdAt")
    ChatMessageDto toMessageDto(ChatMessage chatMessage);

}
