package com.drivefundproject.drive_fund.config.jwt;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.drivefundproject.drive_fund.auth.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Ensure this key is sufficiently long (at least 256 bits for HS256)
    //Same key for signing ang encryption
    private static final String SECRET_KEY = "7a1ca2c8afd7e73cf0c6b4350e89a5d19d9351892d453c8381e09447331457aa";


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        var roles = userDetails.getAuthorities().stream()
              .map(authority -> authority.getAuthority())
              .filter(authority -> authority.startsWith("ROLE_"))
              .collect(Collectors.toList());
            extraClaims.put("roles", roles);
            if (userDetails instanceof User customUser) {
                extraClaims.put("firstName", customUser.getFirstname());               
            }
        return generateToken(extraClaims, userDetails);
    }
    
    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            //.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Set expiration
            .signWith(getSignInKey())
            .compact();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
