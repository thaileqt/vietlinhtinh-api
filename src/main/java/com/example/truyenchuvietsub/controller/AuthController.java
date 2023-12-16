package com.example.truyenchuvietsub.controller;

import com.example.truyenchuvietsub.dto.ChangePasswordDTO;
import com.example.truyenchuvietsub.dto.LoginDTO;
import com.example.truyenchuvietsub.dto.SignupDTO;
import com.example.truyenchuvietsub.dto.TokenDTO;
import com.example.truyenchuvietsub.model.Token;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.RoleRepository;
import com.example.truyenchuvietsub.repository.TokenRepository;
import com.example.truyenchuvietsub.security.TokenGenerator;
import com.example.truyenchuvietsub.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserManager userDetailsManager;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody SignupDTO signupDTO) {
        User user = new User(signupDTO.getUsername(), signupDTO.getPassword(), signupDTO.getEmail());
        userDetailsManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), Collections.EMPTY_LIST);
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));
            System.out.println("Login information: " + authentication.getPrincipal());
            if (authentication == null) {
                throw new BadCredentialsException("Invalid username or password");
            }
            User user = (User) userDetailsManager.loadUserByUsername(loginDTO.getUsername());
            System.out.println(user.getName());
            Token existingToken = tokenRepository.findByUserIdAndTokenType(user.getId(), "access").orElse(null);
            if (existingToken != null) {
                tokenRepository.delete(existingToken);
            }
            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            // return no response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/token")
    public ResponseEntity token(@RequestBody TokenDTO tokenDTO) {
        Token existingToken = tokenRepository.findByTokenValue(tokenDTO.getRefreshToken()).orElseThrow();
        if (!existingToken.getTokenType().equals("refresh")) {
            throw new RuntimeException("Invalid token type");
        }
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        tokenRepository.deleteByUserIdAndTokenType(existingToken.getUserId(), "access");
        // TODO: check if user is active


        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), "access");
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), "refresh");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity changePassword(@RequestHeader("Authorization") String accessToken, @RequestBody ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        // check if new password is same as old password
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password must be different from old password");
        }
        try {
            userDetailsManager.changePassword(changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
