package com.example.truyenchuvietsub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String username;
    private String password;
}
