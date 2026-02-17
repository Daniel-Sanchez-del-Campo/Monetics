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

/**
 * ========================== JWT UTIL ==========================
 * Clase utilitaria para todo lo relacionado con tokens JWT
 * (JSON Web Token).
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES UN JWT?
 * - Es una cadena de texto codificada en Base64 con 3 partes separadas por puntos:
 *   HEADER.PAYLOAD.SIGNATURE
 *
 *   Ejemplo: eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOlt7ImF1dGhvcml0eSI6...  .firma
 *
 *   - HEADER:    algoritmo de firma (HS256) y tipo (JWT)
 *   - PAYLOAD:   datos del usuario (email, rol, fecha expiración) → llamados "claims"
 *   - SIGNATURE: firma digital para verificar que nadie ha modificado el token
 *
 * ¿POR QUÉ USAMOS JWT EN VEZ DE SESIONES?
 * - Las sesiones se almacenan en el servidor → si el servidor se reinicia, se pierden.
 * - JWT es "stateless": el token viaja en cada petición y contiene toda la info necesaria.
 * - El servidor NO necesita guardar nada: solo verifica la firma del token.
 * - Perfecto para arquitecturas API REST + SPA (Angular).
 *
 * ¿ES SEGURO?
 * - El payload NO está cifrado (cualquiera puede leerlo decodificando Base64).
 * - Lo que SÍ está protegido es la INTEGRIDAD: si alguien modifica el payload,
 *   la firma ya no coincide y el token se rechaza.
 * - Por eso NUNCA se debe poner información sensible (contraseñas) en el token.
 *
 * FLUJO:
 *   Login exitoso → generarToken() → se envía al frontend
 *   Cada petición → frontend envía token en header → validarToken() → acceso permitido
 * ==============================================================
 */
@Component
public class JwtUtil {

    /**
     * CLAVE SECRETA para firmar los tokens.
     *
     * ¿QUÉ ES?
     * - Es la "contraseña" que usa el servidor para firmar y verificar tokens.
     * - SOLO el servidor la conoce. Si alguien la obtiene, podría generar tokens falsos.
     *
     * IMPORTANTE PARA PRODUCCIÓN:
     * - Esta clave está hardcodeada aquí solo para desarrollo.
     * - En producción debería estar en variables de entorno o en application.properties
     *   y NUNCA subirse al repositorio.
     */
    private static final String SECRET_KEY = "monetics_secret_key_super_segura_123456";

    /**
     * CLAVE CRIPTOGRÁFICA derivada de la cadena SECRET_KEY.
     *
     * ¿POR QUÉ SE TRANSFORMA?
     * - El algoritmo HS256 necesita un objeto SecretKey, no un String.
     * - Keys.hmacShaKeyFor() convierte nuestra cadena de texto en una clave
     *   criptográfica válida para el algoritmo HMAC-SHA256.
     * - La clave debe tener al menos 256 bits (32 caracteres) para HS256.
     */
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * TIEMPO DE EXPIRACIÓN del token: 10 horas (en milisegundos).
     *
     * ¿POR QUÉ EXPIRAN LOS TOKENS?
     * - Si un token se filtra o es robado, solo será válido durante este tiempo.
     * - Después de 10 horas, el usuario debe volver a hacer login.
     * - Es un balance entre seguridad (tokens cortos) y comodidad (no pedir login constantemente).
     *
     * Cálculo: 1000ms * 60s * 60min * 10h = 36.000.000 ms = 10 horas
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    /**
     * GENERA UN TOKEN JWT a partir de los datos del usuario autenticado.
     *
     * Se llama después de un login exitoso en AuthController.
     *
     * @param userDetails objeto de Spring Security con email y roles del usuario
     * @return String con el token JWT listo para enviar al frontend
     *
     * Ejemplo de uso en AuthController:
     *   String token = jwtUtil.generarToken(authentication.getPrincipal());
     */
    public String generarToken(UserDetails userDetails) {

        // Claims = datos adicionales que se incluyen en el payload del token
        Map<String, Object> claims = new HashMap<>();

        // Guardamos el rol del usuario dentro del token
        // Así cuando el token vuelva en futuras peticiones, sabemos qué rol tiene
        claims.put("rol", userDetails.getAuthorities());

        return crearToken(claims, userDetails.getUsername());
    }

    /**
     * CONSTRUYE el token JWT con todos sus componentes.
     *
     * Estructura del token generado:
     *   {
     *     "rol": [{"authority": "ROLE_USER"}],   ← claims personalizados
     *     "sub": "usuario@email.com",             ← subject (quién es)
     *     "iat": 1708099200,                      ← issued at (cuándo se creó)
     *     "exp": 1708135200                       ← expiration (cuándo expira)
     *   }
     *
     * @param claims datos adicionales a incluir en el token (rol, permisos, etc.)
     * @param subject el identificador del usuario (email en nuestro caso)
     * @return token JWT firmado como String
     */
    private String crearToken(Map<String, Object> claims, String subject) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)          // Datos personalizados (rol)
                .setSubject(subject)        // Email del usuario
                .setIssuedAt(ahora)         // Fecha de creación
                .setExpiration(expiracion)  // Fecha de expiración
                .signWith(key)              // Firma con HMAC-SHA256 usando nuestra clave secreta
                .compact();                 // Construye el token como String
    }

    /**
     * VALIDA un token JWT comprobando dos cosas:
     * 1) Que el username del token coincida con el UserDetails cargado de la BD
     * 2) Que el token NO haya expirado
     *
     * Se llama en cada petición desde JwtAuthenticationFilter.
     *
     * @param token     el JWT recibido en el header Authorization
     * @param userDetails el usuario cargado desde la base de datos
     * @return true si el token es válido, false si no lo es
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = obtenerUsernameDelToken(token);
        return username.equals(userDetails.getUsername())
                && !tokenExpirado(token);
    }

    /**
     * Comprueba si el token ha expirado comparando su fecha de expiración con la fecha actual.
     */
    private boolean tokenExpirado(String token) {
        return obtenerExpiracion(token).before(new Date());
    }

    /**
     * Extrae el USERNAME (email) del token.
     * El username se almacena en el claim "sub" (subject) del JWT.
     */
    public String obtenerUsernameDelToken(String token) {
        return obtenerClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la FECHA DE EXPIRACIÓN del token.
     */
    public Date obtenerExpiracion(String token) {
        return obtenerClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extraer CUALQUIER CLAIM del token.
     *
     * Usa el patrón funcional de Java: recibe una función (claimsResolver)
     * que indica QUÉ claim extraer del conjunto de claims del token.
     *
     * Ejemplos:
     *   obtenerClaim(token, Claims::getSubject)    → devuelve el email
     *   obtenerClaim(token, Claims::getExpiration)  → devuelve la fecha de expiración
     */
    public <T> T obtenerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * PARSEA y VERIFICA la firma del token.
     *
     * ¿QUÉ HACE INTERNAMENTE?
     * 1) Decodifica el token (Base64)
     * 2) Recalcula la firma usando nuestra SECRET_KEY
     * 3) Compara la firma recalculada con la firma del token
     * 4) Si coinciden → el token es auténtico y no fue modificado
     * 5) Si NO coinciden → lanza SignatureException (token inválido/manipulado)
     *
     * @param token el JWT a verificar
     * @return Claims (todos los datos del payload del token)
     */
    private Claims obtenerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)     // Usa nuestra clave para verificar la firma
                .build()
                .parseClaimsJws(token)  // Parsea Y verifica la firma
                .getBody();             // Devuelve el payload (claims)
    }
}
