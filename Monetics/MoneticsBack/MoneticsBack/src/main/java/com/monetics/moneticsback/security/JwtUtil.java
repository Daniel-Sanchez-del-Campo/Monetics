package com.monetics.moneticsback.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    /**
     * Clave secreta (texto normal)
     */
    private static final String SECRET_KEY = "monetics_secret_key_super_segura_123456";

    /**
     * Clave criptográfica válida para HS256
     */
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    public String generarToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", userDetails.getAuthorities());

        return crearToken(claims, userDetails.getUsername());
    }

    private String crearToken(Map<String, Object> claims, String subject) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(key)   // ✅ FORMA CORRECTA
                .compact();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = obtenerUsernameDelToken(token);
        return username.equals(userDetails.getUsername())
                && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return obtenerExpiracion(token).before(new Date());
    }

    public String obtenerUsernameDelToken(String token) {
        return obtenerClaim(token, Claims::getSubject);
    }

    public Date obtenerExpiracion(String token) {
        return obtenerClaim(token, Claims::getExpiration);
    }

    public <T> T obtenerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims obtenerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
