package com.example.truyenchuvietsub.security;

import com.example.truyenchuvietsub.dto.TokenDTO;
import com.example.truyenchuvietsub.model.Token;
import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenGenerator {
    @Autowired
    JwtEncoder accessTokenEncoder;

    @Autowired
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder refreshTokenEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    private String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(60*24, ChronoUnit.MINUTES))
                .subject(user.getId())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    private String createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(user.getId())
                .build();
        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public TokenDTO createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass())
            );
        }

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUserId(user.getId());
        tokenDTO.setAccessToken(createAccessToken(authentication));

        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 7) {
                refreshToken = createRefreshToken(authentication);
            } else {
                refreshToken = jwt.getTokenValue();
            }
        } else {
            refreshToken = createRefreshToken(authentication);
        }

        Token exsitingToken = tokenRepository.findByUserIdAndTokenType(user.getId(), "access").orElse(null);
        if (exsitingToken != null) {
            tokenRepository.delete(exsitingToken);
        }
        Token token = new Token();
        token.setUserId(user.getId());
        token.setTokenType("access");
        token.setTokenValue(tokenDTO.getAccessToken());
        token.setExpirationTime(JwtUtils.extractExpiration(tokenDTO.getAccessToken()));

        tokenDTO.setRefreshToken(refreshToken);
        Token existingRefreshToken = tokenRepository.findByUserIdAndTokenType(user.getId(), "refresh").orElse(null);
        if (existingRefreshToken != null) {
            tokenRepository.delete(existingRefreshToken);
        }
        Token refreshTokenObj = new Token();
        refreshTokenObj.setUserId(user.getId());
        refreshTokenObj.setTokenType("refresh");
        refreshTokenObj.setTokenValue(tokenDTO.getRefreshToken());
        refreshTokenObj.setExpirationTime(JwtUtils.extractExpiration(tokenDTO.getRefreshToken()));

        tokenRepository.save(token);
        tokenRepository.save(refreshTokenObj);

        return tokenDTO;
    }
}
