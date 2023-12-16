package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.UserDTO;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/{id}")
    @PreAuthorize("#user.id == #id")
    public ResponseEntity user(@AuthenticationPrincipal User user, @PathVariable String id) {
        return ResponseEntity.ok(UserDTO.from(userRepository.findById(id).orElseThrow()));
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<UserDTO> user(@PathVariable String username) {
        return ResponseEntity.ok(UserDTO.from(userRepository.findByUsername(username).orElseThrow()));
    }
}
