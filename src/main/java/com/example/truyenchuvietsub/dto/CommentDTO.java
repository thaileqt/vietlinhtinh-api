package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Comment;
import com.example.truyenchuvietsub.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class CommentDTO {
    private String id;
    private String content;
    private UserDTO user;
    private String chapterId;
    private Date createdAt;

    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(UserDTO.from(comment.getUser()))
                .chapterId(comment.getChapter().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
