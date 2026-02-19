package com.monetics.moneticsback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ========================== PASSWORD CONFIG ==========================
 * Configuración del cifrado de contraseñas con BCrypt.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES BCrypt?
 * - Es un algoritmo de hashing diseñado específicamente para contraseñas.
 * - Convierte "miContraseña123" → "$2a$10$N9qo8uLOickgx2ZMRZoMy..."
 *   (una cadena irreversible de ~60 caracteres).
 *
 * ¿POR QUÉ BCrypt Y NO MD5 O SHA?
 * - BCrypt es LENTO a propósito (incluye un "factor de coste").
 *   Esto hace que ataques de fuerza bruta sean mucho más lentos.
 * - Cada hash incluye un "salt" (valor aleatorio) automáticamente.
 *   Esto significa que dos usuarios con la misma contraseña tendrán
 *   hashes DIFERENTES, lo que previene ataques con tablas rainbow.
 * - MD5 y SHA son rápidos (diseñados para verificar archivos, no contraseñas).
 *
 * ¿CÓMO FUNCIONA LA COMPARACIÓN?
 * - BCrypt NO descifra el hash (es irreversible).
 * - En su lugar, hashea la contraseña introducida y compara los hashes:
 *   passwordEncoder.matches("miContraseña123", hashAlmacenado) → true/false
 *
 * ¿DÓNDE SE USA?
 * 1) En UsuarioService → al CREAR un usuario, se hashea la contraseña antes de guardarla:
 *    usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));
 *
 * 2) En SecurityConfig → el DaoAuthenticationProvider usa este bean para COMPARAR
 *    la contraseña del login con la almacenada en la BD.
 *
 * ¿POR QUÉ ESTÁ EN UNA CLASE SEPARADA?
 * - Para evitar dependencias circulares en Spring:
 *   SecurityConfig necesita PasswordEncoder, y UsuarioService también.
 *   Si PasswordEncoder estuviera definido en SecurityConfig, se crearía
 *   un ciclo de dependencias. Al ponerlo en su propia @Configuration,
 *   Spring lo resuelve sin problemas.
 * ==================================================================
 */
@Configuration
public class PasswordConfig {

    /**
     * Bean de PasswordEncoder que Spring inyectará donde sea necesario.
     *
     * BCryptPasswordEncoder con el factor de coste por defecto (10).
     * El factor de coste determina cuántas rondas de hashing se aplican:
     *   - Factor 10 → ~100ms por hash (bueno para desarrollo y producción)
     *   - Factor 12 → ~400ms por hash (más seguro, más lento)
     *
     * @return instancia de BCryptPasswordEncoder que se usa en todo el sistema
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
