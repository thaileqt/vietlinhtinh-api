package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.ReplyMessage;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReplyMessageDTO {
    private String id;
    private String content;
    private UserDTO sender;
    private Date createdAt;
    private Date updatedAt;
    private String messageId;

    public static ReplyMessageDTO from(ReplyMessage replyMessage) {
        return ReplyMessageDTO.builder()
                .id(replyMessage.getId())
                .content(replyMessage.getContent())
                .sender(UserDTO.from(replyMessage.getSender()))
                .createdAt(replyMessage.getCreatedAt())
                .updatedAt(replyMessage.getUpdatedAt())
                .build();
    }
}
