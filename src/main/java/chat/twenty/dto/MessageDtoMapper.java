package chat.twenty.dto;

import chat.twenty.domain.ChatMessage;
import chat.twenty.domain.TwentyMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageDtoMapper {
    MessageDtoMapper INSTANCE = Mappers.getMapper(MessageDtoMapper.class);

    @Mapping(target = "roomId", source = "roomId") // 부모 필드들은 명시해줘야 정상작동함.
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "isGptChat", source = "gptChat")
    TwentyMessage toTwentyMessage(TwentyMessageDto twentyMessageDto);
    TwentyMessageDto toTwentyMessageDto(TwentyMessage twentyMessage);


    @Mapping(target = "roomId", source = "roomId") // 부모 필드들은 명시해줘야 정상작동함.
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "isGptChat", source = "gptChat")
    ChatMessage toChatMessage(ChatMessageDto chatMessageDto);
    ChatMessageDto toChatMessageDto(ChatMessage chatMessage);

}
