package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.MessageDTO;
import com.example.truyenchuvietsub.dto.ReplyMessageDTO;
import com.example.truyenchuvietsub.model.Message;
import com.example.truyenchuvietsub.model.ReplyMessage;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.MessageRepository;
import com.example.truyenchuvietsub.repository.ReplyMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserManager userManager;
    @Autowired
    private ReplyMessageRepository replyMessageService;
    @Autowired
    private MongoTemplate mongoTemplate;

    // create get message with pagination
    // create get reply message with pagination
    // create get message by id

    public List<MessageDTO> getRecentMessagesOfUser(String username, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String userId = ((User) userManager.loadUserByUsername(username)).getId();
        Query query = new Query();
        query.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("sender._id").is(userId).and("receiver._id").is(userId),
                        Criteria.where("receiver._id").is(userId)
                )
        );
        query.with(pageable);
        List<Message> messages = mongoTemplate.find(query, Message.class);
        return messages.stream().map(MessageDTO::from).toList();
    }



    public MessageDTO createMessage(MessageDTO messageDTO, Authentication authentication) {
        Message message = new Message();
        message.setSender((User) userManager.loadUserByUsername(authentication.getName()));
        message.setReceiver((User) userManager.loadUserByUsername(messageDTO.getReceiver().getUsername()));
        message.setContent(messageDTO.getContent());
        message = messageRepository.save(message);
        return MessageDTO.from(message);
    }

    public ReplyMessageDTO createReplyMessage(ReplyMessageDTO replyMessageDTO, Authentication authentication) {
        // create reply message
        ReplyMessage replyMessage = new ReplyMessage();
        replyMessage.setSender((User) userManager.loadUserByUsername(authentication.getName()));
        replyMessage.setContent(replyMessageDTO.getContent());
        replyMessage = replyMessageService.save(replyMessage);

        // add reply message to message
        Message message = messageRepository.findById(replyMessageDTO.getMessageId()).orElse(null);
        assert message != null;
        message.getReplyMessages().add(replyMessage);
        messageRepository.save(message);

        return ReplyMessageDTO.from(replyMessage);
    }


    public void deleteMessage(String id, Authentication authentication) {
        // TODO: check if the user is the sender or the receiver
        User user = (User) userManager.loadUserByUsername(authentication.getName());
//        if (!user.getId().equals(messageRepository.findById(id).orElse(null).getSender().getId())) {
//            throw new RuntimeException("You are not the sender of this message");
//        } else if (!user.getId().equals(messageRepository.findById(id).orElse(null).getReceiver().getId())) {
//            throw new RuntimeException("You are not the receiver of this message");
//        }
        Message message = messageRepository.findById(id).orElse(null);
        assert message != null;
        for (ReplyMessage replyMessage : message.getReplyMessages()) {
            deleteReplyMessage(replyMessage.getId(), authentication);
        }
        messageRepository.delete(message);
    }

    public void deleteReplyMessage(String id, Authentication authentication) {
        // TODO: check if the user is the sender or the receiver
        ReplyMessage replyMessage = replyMessageService.findById(id).orElse(null);
        assert replyMessage != null;
        replyMessageService.delete(replyMessage);
    }
}
