package com.monetics.moneticsback.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad central para la gestión de JWT (JSON Web Tokens).
 *
 * Esta clase se encarga de:
 * - Generar tokens JWT
 * - Validar tokens
 * - Extraer información del token (email, roles, expiración)
 *
 * Es utilizada tanto en el login como en el filtro JWT.
 */
@Component
public class JwtUtil {

    /**
     * Clave secreta para firmar los tokens.
     *
     * IMPORTANTE:
     * - En un proyecto real esto estaría en variables de entorno
     * - Aquí se deja en código por simplicidad académica
     */
    private static final String SECRET_KEY = "monetics_secret_key_super_segura";

    /**
     * Tiempo de validez del token.
     *
     * En este caso: 10 horas.
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    /* ============================
       GENERACIÓN DEL TOKEN
       ============================ */

    /**
     * Genera un token JWT a partir de los datos del usuario.
     *
     * @param userDetails usuario autenticado
     * @return token JWT en formato String
     */
    public String generarToken(UserDetails userDetails) {

        // Claims = información que guardamos dentro del token
        Map<String, Object> claims = new HashMap<>();

        // Guardamos el rol del usuario dentro del token
        claims.put("rol", userDetails.getAuthorities());

        return crearToken(claims, userDetails.getUsername());
    }

    /**
     * Crea el token JWT firmándolo con la clave secreta.
     */
    private String crearToken(Map<String, Object> claims, String subject) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)            // datos personalizados
                .setSubject(subject)          // normalmente el username (email)
                .setIssuedAt(ahora)            // fecha de creación
                .setExpiration(expiracion)    // fecha de expiración
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // firma
                .compact();
    }

    /* ============================
       VALIDACIÓN DEL TOKEN
       ============================ */

    /**
     * Valida un token comprobando:
     * - que pertenece al usuario correcto
     * - que no está expirado
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = obtenerUsernameDelToken(token);
        return username.equals(userDetails.getUsername())
                && !tokenExpirado(token);
    }

    /**
     * Comprueba si el token ha expirado.
     */
    private boolean tokenExpirado(String token) {
        return obtenerExpiracion(token).before(new Date());
    }

    /* ============================
       EXTRACCIÓN DE DATOS
       ============================ */

    /**
     * Obtiene el username (email) guardado en el token.
     */
    public String obtenerUsernameDelToken(String token) {
        return obtenerClaim(token, Claims::getSubject);
    }

    /**
     * Obtiene la fecha de expiración del token.
     */
    public Date obtenerExpiracion(String token) {
        return obtenerClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para obtener cualquier claim del token.
     */
    public <T> T obtenerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * Aquí es donde se valida la firma del token.
     */
    private Claims obtenerTodosLosClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
