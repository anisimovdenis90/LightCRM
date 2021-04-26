package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.lightcrm.entities.ChatMessage;
import ru.lightcrm.utils.MessageStatus;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Long countChatMessageBySenderIdAndRecipientIdAndMessageStatus(@NotNull Long senderId, @NotNull Long recipientId, @NotNull MessageStatus status);
    List<ChatMessage> findByChatId(@NotNull String chatId);
    @Query("SELECT m FROM ChatMessage m WHERE m.timestamp IN " +
            "(SELECT MAX(m.timestamp) FROM ChatMessage m WHERE m.chatId = ?1)")
    Optional<ChatMessage> findByChatIdAndMaxTimestamp(@NotNull String chatId);
}
