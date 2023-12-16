package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.CommentDTO;
import com.example.truyenchuvietsub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable String id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

    @GetMapping("/get-by-user-id/{userId}")
    public ResponseEntity<List<CommentDTO>> getCommentByUserId(@PathVariable String userId) {
        List<CommentDTO> commentDTO = commentService.getCommentByUserId(userId);
        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

    @PostMapping("/create")
    public void createComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        commentService.createComment(commentDTO, authentication);
    }

    @PostMapping("/delete-by-id")
    public void deleteComment(@RequestBody String id) {
        commentService.deleteComment(id);
    }

    @PutMapping("/update")
    public void updateComment(@RequestBody CommentDTO commentDTO) {
        commentService.updateComment(commentDTO);
    }
}
