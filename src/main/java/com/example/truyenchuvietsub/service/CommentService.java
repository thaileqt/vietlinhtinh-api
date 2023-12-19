package com.example.truyenchuvietsub.service;

import com.example.truyenchuvietsub.dto.CommentDTO;
import com.example.truyenchuvietsub.model.Comment;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private UserManager userManager;

    public CommentDTO createComment(CommentDTO commentDTO, Authentication authentication) {
        Comment comment = new Comment();
        comment.setChapter(chapterService.getChapterById(commentDTO.getChapterId()).orElseThrow());
        comment.setUser((User) userManager.loadUserByUsername(authentication.getName()));
        comment.setContent(commentDTO.getContent());
        return CommentDTO.from(commentRepository.save(comment));
    }

    public CommentDTO getCommentById(String id) {
        return CommentDTO.from(commentRepository.findById(id).orElseThrow());
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }

    public void updateComment(CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentDTO.getId()).orElseThrow();
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
    }

    public List<CommentDTO> getCommentByUserId(String userId) {
        return commentRepository.findAllByUser_Id(userId).stream().map(CommentDTO::from).toList();
    }
}
