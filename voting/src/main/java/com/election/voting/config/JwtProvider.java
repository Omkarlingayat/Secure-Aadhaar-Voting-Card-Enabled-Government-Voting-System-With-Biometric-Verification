package com.election.voting.config;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtProvider {

    private SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    private static final long JWT_EXPIRATION = 6000000; // 10 minutes in milliseconds

    public String generateToken(Authentication auth) {
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .claim("username", auth.getName())
                .signWith(key)
                .compact();

        return jwt;
    }

    public String getUsernameFromToken(String jwt) {
        jwt = jwt.substring(7); // Remove "Bearer " prefix

        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        return String.valueOf(claims.get("username"));
    }

}
