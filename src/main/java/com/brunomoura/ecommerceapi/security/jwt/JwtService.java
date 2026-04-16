package com.brunomoura.ecommerceapi.security.jwt;

import com.brunomoura.ecommerceapi.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signKey = Keys.hmacShaKeyFor(jwtProperties.getSecret());
    }

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        return buildToken(claims, userDetails);
    }

    public String extractSubject(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    Claims extractAllClaims(String token) {

        return Jwts.parserBuilder().setSigningKey(this.signKey).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + this.jwtProperties.getExpiration());
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails) {

        return Jwts.builder().
                setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(this.signKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
