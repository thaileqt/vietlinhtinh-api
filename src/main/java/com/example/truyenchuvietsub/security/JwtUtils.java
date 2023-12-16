package com.example.truyenchuvietsub.security;

import com.example.truyenchuvietsub.model.User;
import com.example.truyenchuvietsub.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;
import java.util.Date;

public class JwtUtils {
    public static Instant extractExpiration(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationDate = claimsSet.getExpirationTime();
            return expirationDate.toInstant();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String extractUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getStringClaim("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
