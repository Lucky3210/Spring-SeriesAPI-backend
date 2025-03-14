package com.Series.SeriesAPI.Auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// THIS CLASS IS A UTILITY CLASS FOR JWT ACCESS TOKEN
@Service
public class JwtService {

    private static final String SECRET_KEY = "3TAY/ml4UhUmUwhWQG8IgWY3C/KDb60L0tZOvNvpo+o=";

    // extract username from JWT
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // extract information from JWT
    @SuppressWarnings("deprecation")
    private Claims extractAllClaims(String token) {
        // this is Jwts.parser and not Jwts.parserBuilder
        return Jwts.parser()
                .setSigningKey(getSignInKey()) // Use the signing key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // decode and get the key
    private Key getSignInKey(){

        // decode Secret key
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        if (keyBytes.length < 32) { // 32 bytes = 256 bits
            throw new IllegalArgumentException("Invalid key length! Key must be at least 256 bits for HS256.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // generate token using Jwt utility class and returns token as String
    @SuppressWarnings("deprecation")
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 2000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // if token is valid by checking if token is expired for current user
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // if token is expired
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // get expiration date from token
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
}
