package com.example.truyenchuvietsub.dto;

import com.example.truyenchuvietsub.model.Marker;
import com.example.truyenchuvietsub.model.Role;
import com.example.truyenchuvietsub.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class UserDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private String cover;
    private Set<Role> roles;

    public static UserDTO from(User user) {

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .cover(user.getCover())
                .build();
    }

    public UserDTO build() {
        return this;
    }
}

