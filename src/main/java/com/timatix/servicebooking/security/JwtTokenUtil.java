package com.timatix.servicebooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret:timatixSecretKeyForDevelopmentOnlyNotForProductionUseBase64EncodedString}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // For development, create key from string
        // In production, use a proper base64 encoded secret
        byte[] keyBytes;
        if (secret.length() < 32) {
            // Ensure minimum key length for HS512
            String paddedSecret = secret + "PaddingToMeetMinimumKeyLength32Characters";
            keyBytes = paddedSecret.substring(0, 64).getBytes();
        } else {
            try {
                // Try to decode as base64 first
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (Exception e) {
                // Fall back to using the string directly
                keyBytes = secret.getBytes();
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()  // Updated from parserBuilder()
                .verifyWith(getSigningKey())  // Updated method
                .build()
                .parseSignedClaims(token)  // Updated from parseClaimsJws()
                .getPayload();  // Updated from getBody()
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add custom claims if needed
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }

    public String generateTokenWithClaims(UserDetails userDetails, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .claims(claims)  // Updated from setClaims()
                .subject(subject)  // Updated from setSubject()
                .issuedAt(now)  // Updated from setIssuedAt()
                .expiration(expiryDate)  // Updated from setExpiration()
                .signWith(getSigningKey())  // Updated - no algorithm needed
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            claims.put(Claims.ISSUED_AT, new Date());
            return createToken(claims, claims.getSubject());
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return null;
        }
    }

    // Utility method to extract all claims for debugging
    public Claims extractAllClaims(String token) {
        return getAllClaimsFromToken(token);
    }

    // Method to check if token is valid (not expired and properly formatted)
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}