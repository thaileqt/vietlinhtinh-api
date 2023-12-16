package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.MessageDTO;
import com.example.truyenchuvietsub.dto.ReplyMessageDTO;
import com.example.truyenchuvietsub.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/{username}")
    public List<MessageDTO> getTopRecentMessages(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return messageService.getRecentMessagesOfUser(username, page, size);
    }

    @PostMapping("/create-message")
    public MessageDTO createMessage(@RequestBody MessageDTO messageDTO, Authentication authentication) {
        return messageService.createMessage(messageDTO, authentication);
    }

    @PostMapping("/create-reply-message")
    public ReplyMessageDTO createReplyMessage(@RequestBody ReplyMessageDTO replyMessageDTO, Authentication authentication) {
        return messageService.createReplyMessage(replyMessageDTO, authentication);
    }

    @DeleteMapping("/delete-messge/{id}")
    public void deleteMessage(@PathVariable String id, Authentication authentication) {
        messageService.deleteMessage(id, authentication);
    }

    @DeleteMapping("/delete-reply-message/{id}")
    public void deleteReplyMessage(@PathVariable String id, Authentication authentication) {
        messageService.deleteMessage(id, authentication);
    }




}
