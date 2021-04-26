package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.ChatRoom;
import ru.lightcrm.entities.dtos.ChatMessageDto;
import ru.lightcrm.entities.dtos.ChatRoomDto;
import ru.lightcrm.entities.dtos.ProfileMiniDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.ChatMessageRepository;
import ru.lightcrm.repositories.ChatRoomRepository;
import ru.lightcrm.services.interfaces.ChatRoomService;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.utils.MessageStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProfileService profileService;

    @Override
    public Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist) {
                        return Optional.empty();
                    }
                    String chatId = String.format("%d_%d", senderId, recipientId);

                    ChatRoom senderRecipient = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();

                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }

    @Override
    public List<ChatRoomDto> getChatsDto(Long senderId) {
        List<ChatRoomDto> chatRoomDtoList = chatRoomRepository.findAllBySenderId(senderId).stream().map(ChatRoomDto::new).collect(Collectors.toList());
        for (ChatRoomDto chatRoomDto : chatRoomDtoList) {
            ChatMessageDto chatMessageDto = chatMessageRepository.findByChatIdAndMaxTimestamp(chatRoomDto.getChatId()).map(ChatMessageDto::new)
                    .orElse(null);
            chatRoomDto.setLastMessage(chatMessageDto);
            chatRoomDto.setRecipientName(getRecipientNameFromProfileById(chatRoomDto.getRecipientId()));
            chatRoomDto.setUnreadMessageCount(chatMessageRepository.countChatMessageBySenderIdAndRecipientIdAndMessageStatus(chatRoomDto.getRecipientId(), chatRoomDto.getSenderId(), MessageStatus.RECEIVED));
        }
        return chatRoomDtoList;
    }

    @Override
    public ChatRoomDto getChatByIdAndRecipientId(String chatId, Long recipientId) {
        ChatRoomDto chatRoomDto = chatRoomRepository.findByChatIdAndRecipientId(chatId, recipientId).map(ChatRoomDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Комната с id %s отсутствует", chatId)));
        chatRoomDto.setRecipientName(getRecipientNameFromProfileById(recipientId));
        ChatMessageDto chatMessageDto = chatMessageRepository.findByChatIdAndMaxTimestamp(chatRoomDto.getChatId()).map(ChatMessageDto::new)
                .orElse(null);
        chatRoomDto.setLastMessage(chatMessageDto);
        return chatRoomDto;
    }

    private String getRecipientNameFromProfileById(Long id) {
        ProfileMiniDto profileDto = profileService.findMiniDtoById(id);
        return String.format("%s %s", profileDto.getFirstname(), profileDto.getLastname());
    }
}
