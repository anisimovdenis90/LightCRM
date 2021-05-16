package ru.lightcrm.entities;

import lombok.*;
import ru.lightcrm.utils.MessageStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp")
    private OffsetDateTime timestamp;

    @Column(name = "message_status")
    private MessageStatus messageStatus;

    @Builder
    public ChatMessage(Long id, String chatId, Long senderId, Long recipientId, String senderName, String recipientName, String content, OffsetDateTime timestamp, MessageStatus messageStatus) {
        super(id);
        this.chatId = chatId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.content = content;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
    }
}
