package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Message;
import com.example.truyenchuvietsub.model.ReplyMessage;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MessageDTO {
    private String id;
    private String content;
    private UserDTO sender;
    private UserDTO receiver;
    private List<ReplyMessageDTO> replyMessages;
    private Date createdAt;
    private Date updatedAt;

    public static MessageDTO from(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(UserDTO.from(message.getSender()))
                .receiver(UserDTO.from(message.getReceiver()))
                .replyMessages(message.getReplyMessages().stream().map(ReplyMessageDTO::from).toList())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
