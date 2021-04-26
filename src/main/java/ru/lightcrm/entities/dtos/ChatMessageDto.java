package ru.lightcrm.entities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lightcrm.entities.ChatMessage;
import ru.lightcrm.utils.CustomDateDeserializer;
import ru.lightcrm.utils.MessageStatus;

import java.time.OffsetDateTime;

@ApiModel(description = "Сообщение")
@Data
@NoArgsConstructor
@JsonRootName("ChatMessageDto")
public class ChatMessageDto {

    @ApiModelProperty(notes = "Идентификатор сообщения", example = "1", required = true, position = 1)
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(notes = "Идентификатор комнаты", example = "1_2", required = true, position = 2)
    @JsonProperty("chatId")
    private String chatId;

    @ApiModelProperty(notes = "Идентификатор отправителя", example = "1", required = true, position = 3)
    @JsonProperty("senderId")
    private Long senderId;

    @ApiModelProperty(notes = "Идентификатор получателя", example = "1", required = true, position = 4)
    @JsonProperty("recipientId")
    private Long recipientId;

    @ApiModelProperty(notes = "Имя отправителя", example = "Иван Петров", required = true, position = 5)
    @JsonProperty("senderName")
    private String senderName;

    @ApiModelProperty(notes = "Имя получателя", example = "Петр Иванов", required = true, position = 6)
    @JsonProperty("recipientName")
    private String recipientName;

    @ApiModelProperty(notes = "Текст сообщения", example = "Привет, username!", required = true, position = 7)
    @JsonProperty("content")
    private String content;

    @ApiModelProperty(notes = "Дата отправки сообщения", required = true, position = 8)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonProperty("timestamp")
    private OffsetDateTime timestamp;

    @ApiModelProperty(notes = "Статус сообщения", required = true, position = 9)
    @JsonProperty("messageStatus")
    private MessageStatus messageStatus;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.chatId = chatMessage.getChatId();
        this.senderId = chatMessage.getSenderId();
        this.recipientId = chatMessage.getRecipientId();
        this.senderName = chatMessage.getSenderName();
        this.recipientName = chatMessage.getRecipientName();
        this.content = chatMessage.getContent();
        this.timestamp = chatMessage.getTimestamp();
        this.messageStatus = chatMessage.getMessageStatus();
    }
}
