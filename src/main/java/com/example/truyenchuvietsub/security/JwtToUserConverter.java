package com.example.truyenchuvietsub.security;

import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.TokenRepository;
import com.example.truyenchuvietsub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        User user = userRepository.findById(jwt.getSubject()).orElse(null);
        assert user != null;
        // check if token exist
        if (tokenRepository.findByTokenValue(jwt.getTokenValue()).isEmpty()) {
            return null;
        }
        user.setId(jwt.getSubject());
        user.setUsername(user.getUsername());
        user.setName(user.getName());
        user.setPassword(user.getPassword());
        user.setRoles(user.getRoles());


        return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());

    }

}
