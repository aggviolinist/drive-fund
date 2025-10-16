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

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import io.jsonwebtoken.security.AeadAlgorithm; // JWE imports
import io.jsonwebtoken.security.KeyAlgorithm; // JWE imports
import io.jsonwebtoken.security.RsaPublicJwk;


@Service
public class JwtService {

    // Ensure this key is sufficiently long (at least 256 bits for HS256)
    //Same key for signing ang encryption
    private static final String SECRET_KEY = "7a1ca2c8afd7e73cf0c6b4350e89a5d19d9351892d453c8381e09447331457aa";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private static final KeyAlgorithm<PublicKey, PrivateKey> KEY_MGMT_ALGORITHM = Jwts.KEY.RSA_OAEP_256;
    private static final AeadAlgorithm CONTENT_ENCRYPTION_ALGORITHM = Jwts.ENC.A256GCM;


    //When spring starts up, it first loads the public and private key to memory hence, reducing performance issues
    public JwtService() throws Exception{
        this.privateKey = KeyUtils.loadPrivateKey("keys/local-only/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local-only/public_key.pem");
    }


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
    
    public String generateSignedToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            //.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Set expiration
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(
        Map<String, Object> extraClaims, UserDetails userDetails
    ){
        String signedJwt = generateSignedToken(extraClaims, userDetails);
    
        return generateEncryptedToken(signedJwt);
    }

    public String generateEncryptedToken(String signedJwt){
        return Jwts.builder()
             .content(signedJwt, "application/jwt")
             .encryptWith(publicKey, KEY_MGMT_ALGORITHM,CONTENT_ENCRYPTION_ALGORITHM)
             .compact();
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
        //1. Decrypt outer token(JWE) to get inner signed token(JWS)
        String signedJwt = decryptToken(token); 

        //2. Verify and parse inner signed token(JWS) using symmetric key
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(signedJwt)
            .getPayload();
    }
    private String decryptToken(String encryptedJwt){
        //Decrypt JWE using Private Key
        return new String(
            Jwts.parser()
               .decryptWith(privateKey)
               .build()
               .parseEncryptedContent(encryptedJwt)
               .getPayload(),
            StandardCharsets.UTF_8
        );
    }
}
