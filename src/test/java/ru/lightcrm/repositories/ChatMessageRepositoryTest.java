package ru.lightcrm.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.ChatMessage;
import ru.lightcrm.utils.MessageStatus;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class ChatMessageRepositoryTest {
    private static final Long SENDER_ID = 1L;
    private static final Long RECIPIENT_ID = 2L;
    private static final String CHAT_ID = "1_2";

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void countChatMessageBySenderIdAndRecipientIdAndMessageStatusTest() {
        Long countMessage = chatMessageRepository.countChatMessageBySenderIdAndRecipientIdAndMessageStatus(SENDER_ID, RECIPIENT_ID, MessageStatus.RECEIVED);

        Assertions.assertNotNull(countMessage);
        Assertions.assertEquals(1L, countMessage);
    }

    @Test
    public void findByChatIdTest() {
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(CHAT_ID);

        Assertions.assertNotNull(chatMessages);
        Assertions.assertEquals(2, chatMessages.size());
    }

    @Test
    public void findByChatIdAndMaxTimestampTest() {
        ChatMessage chatMessage = chatMessageRepository.findByChatIdAndMaxTimestamp(CHAT_ID).orElse(null);

        Assertions.assertNotNull(chatMessage);
        Assertions.assertEquals(RECIPIENT_ID, chatMessage.getSenderId());
    }
}
