package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Comment;
import com.example.truyenchuvietsub.model.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDTO {
    private String id;
    private String content;
    private UserDTO userDTO;
    private String chapterId;

    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userDTO(UserDTO.from(comment.getUser()))
                .chapterId(comment.getChapter().getId())
                .build();
    }
}
