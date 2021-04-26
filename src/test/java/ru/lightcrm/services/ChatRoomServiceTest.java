package ru.lightcrm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.ChatMessage;
import ru.lightcrm.entities.ChatRoom;
import ru.lightcrm.entities.dtos.ChatRoomDto;
import ru.lightcrm.entities.dtos.ProfileMiniDto;
import ru.lightcrm.repositories.ChatMessageRepository;
import ru.lightcrm.repositories.ChatRoomRepository;
import ru.lightcrm.services.interfaces.ChatRoomService;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.utils.MessageStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class ChatRoomServiceTest {
    private static final Long SENDER_ID = 1L;
    private static final Long RECIPIENT_ID = 2L;
    private static final String CHAT_ID = "1_2";

    @Autowired
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    @MockBean
    private ChatRoomRepository chatRoomRepository;
    @MockBean
    private ProfileService profileService;

    @Test
    public void getChatIdTest() {
        Mockito.doReturn(generateMockChatRoom()).when(chatRoomRepository)
                .findBySenderIdAndRecipientId(SENDER_ID, RECIPIENT_ID);

        String chatId = chatRoomService.getChatId(SENDER_ID, RECIPIENT_ID, false).orElse(null);
        Assertions.assertEquals(CHAT_ID, chatId);
    }

    @Test
    public void getChatsDtoTest() {
        Mockito.doReturn(generateMockChatRoomList()).when(chatRoomRepository)
                .findAllBySenderId(SENDER_ID);
        Mockito.doReturn(generateMockMessage()).when(chatMessageRepository)
                .findByChatIdAndMaxTimestamp(CHAT_ID);
        Mockito.doReturn(generateMockProfileMiniDto()).when(profileService)
                .findMiniDtoById(RECIPIENT_ID);

        List<ChatRoomDto> chatRoomDtoList = chatRoomService.getChatsDto(SENDER_ID);
        Assertions.assertNotNull(chatRoomDtoList);
        Assertions.assertEquals(1, chatRoomDtoList.size());
    }

    @Test
    public void getChatByIdAndRecipientIdTest() {
        Mockito.doReturn(generateMockChatRoom()).when(chatRoomRepository)
                .findByChatIdAndRecipientId(CHAT_ID, RECIPIENT_ID);
        Mockito.doReturn(generateMockProfileMiniDto()).when(profileService)
                .findMiniDtoById(RECIPIENT_ID);
        Mockito.doReturn(generateMockMessage()).when(chatMessageRepository)
                .findByChatIdAndMaxTimestamp(CHAT_ID);

        ChatRoomDto chatRoomDto = chatRoomService.getChatByIdAndRecipientId(CHAT_ID, RECIPIENT_ID);

        Assertions.assertNotNull(chatRoomDto);
        Assertions.assertEquals(RECIPIENT_ID, chatRoomDto.getRecipientId());
        Assertions.assertEquals(CHAT_ID, chatRoomDto.getChatId());
    }

    private Optional<ChatMessage> generateMockMessage() {
        return Optional.of(ChatMessage.builder()
                .id(1L)
                .chatId(CHAT_ID)
                .senderId(SENDER_ID)
                .recipientId(RECIPIENT_ID)
                .senderName("Виктор Викторов")
                .recipientName("Петр Петров")
                .content("Превед, кросафчег!")
                .timestamp(OffsetDateTime.now())
                .messageStatus(MessageStatus.DELIVERED)
                .build());
    }

    private Optional<ChatRoom> generateMockChatRoom() {
        return Optional.of(ChatRoom.builder()
                .id(1L)
                .chatId(CHAT_ID)
                .senderId(SENDER_ID)
                .recipientId(RECIPIENT_ID)
                .build());
    }

    private List<ChatRoom> generateMockChatRoomList() {
        return List.of(ChatRoom.builder()
                .id(1L)
                .chatId(CHAT_ID)
                .senderId(SENDER_ID)
                .recipientId(RECIPIENT_ID)
                .build());
    }

    private ProfileMiniDto generateMockProfileMiniDto() {
        return ProfileMiniDto.builder()
                .firstname("Петр")
                .lastname("Петров")
                .middlename("Петрович")
                .staffUnitName("Администратор")
                .employmentDate(OffsetDateTime.now())
                .departmentNames(List.of("IT"))
                .build();
    }
}
