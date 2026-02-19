package com.monetics.moneticsback.security;

import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * ========================== CUSTOM USER DETAILS SERVICE ==========================
 * Implementación personalizada de la interfaz UserDetailsService de Spring Security.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES UserDetailsService?
 * - Es una interfaz de Spring Security con un solo método: loadUserByUsername().
 * - Spring Security la llama AUTOMÁTICAMENTE cuando necesita autenticar un usuario.
 * - Es el "puente" entre nuestra base de datos y Spring Security.
 *
 * ¿POR QUÉ NECESITAMOS ESTA CLASE?
 * - Spring Security no sabe cómo acceder a nuestra BD ni qué tabla tiene los usuarios.
 * - Nosotros le decimos: "busca en la tabla 'usuarios' por el campo 'email'
 *   y devuelve un objeto UserDetails con email, contraseña hasheada y roles".
 *
 * ¿CUÁNDO SE LLAMA?
 * 1) Durante el LOGIN: el DaoAuthenticationProvider (configurado en SecurityConfig)
 *    llama a loadUserByUsername(email) para obtener los datos del usuario de la BD
 *    y comparar la contraseña.
 * 2) Durante la VALIDACIÓN DEL JWT: el JwtAuthenticationFilter llama a
 *    loadUserByUsername(email) para cargar los datos actualizados del usuario
 *    y verificar que sigue existiendo y activo.
 *
 * ¿QUÉ ES UserDetails?
 * - Es la interfaz estándar de Spring Security que representa un usuario autenticado.
 * - Contiene: username, password, authorities (roles), y flags de estado (enabled, etc.)
 * - Spring Security SOLO entiende UserDetails, no nuestra entidad Usuario.
 *   Por eso convertimos Usuario → UserDetails en este servicio.
 *
 * FLUJO:
 *   Login → DaoAuthenticationProvider → loadUserByUsername(email)
 *         → Busca en BD → Convierte a UserDetails → Spring compara contraseñas
 * ==================================================================================
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Servicio de usuarios para acceder a la base de datos
    private final UsuarioService usuarioService;

    // Inyección de dependencias por constructor
    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * ==================== loadUserByUsername ====================
     * Método que Spring Security llama automáticamente cuando necesita
     * los datos de un usuario para autenticarlo.
     *
     * IMPORTANTE: Aunque se llama "loadUserByUsername", en nuestro caso
     * el "username" es el EMAIL del usuario (es lo que usamos para identificarlos).
     *
     * ¿QUÉ DEVUELVE?
     * - Un objeto UserDetails de Spring Security con:
     *   · username  → email del usuario
     *   · password  → contraseña HASHEADA (BCrypt) desde la BD
     *   · enabled   → si el usuario está activo (campo 'activo' de la entidad)
     *   · authorities → roles del usuario convertidos a GrantedAuthority
     *
     * ¿QUÉ PASA SI EL USUARIO NO EXISTE?
     * - Se lanza UsernameNotFoundException y Spring Security devuelve 401.
     *
     * @param username email del usuario (lo recibe de AuthController o JwtFilter)
     * @return UserDetails que Spring Security usa para autenticación/autorización
     * @throws UsernameNotFoundException si el email no existe en la BD
     * ==================================================================
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Buscamos al usuario en la BD por email
        // Usamos el método que devuelve la ENTIDAD (no el DTO)
        // porque necesitamos acceso a la contraseña hasheada
        Usuario usuario =
                usuarioService.obtenerUsuarioEntidadPorEmail(username);

        // Convertimos el rol de nuestra entidad (RolUsuario enum)
        // al formato que Spring Security entiende (GrantedAuthority)
        //
        // ¿QUÉ ES GrantedAuthority?
        // - Es la interfaz de Spring Security que representa un permiso/rol.
        // - SimpleGrantedAuthority es la implementación más simple: un String con el nombre del rol.
        // - Ejemplo: RolUsuario.ROLE_USER → SimpleGrantedAuthority("ROLE_USER")
        //
        // ¿POR QUÉ EL PREFIJO "ROLE_"?
        // - Es una convención de Spring Security. Cuando usamos hasRole("USER"),
        //   Spring automáticamente busca una authority llamada "ROLE_USER".
        //   Por eso nuestro enum ya incluye el prefijo: ROLE_USER, ROLE_MANAGER, ROLE_ADMIN.
        GrantedAuthority authority =
                new SimpleGrantedAuthority(usuario.getRol().name());

        // Construimos y devolvemos el UserDetails estándar de Spring Security
        // Spring comparará automáticamente la contraseña del formulario con esta
        // usando BCryptPasswordEncoder (configurado en SecurityConfig)
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),      // username → usamos email como identificador
                usuario.getPassword(),   // password → contraseña hasheada con BCrypt
                usuario.getActivo(),     // enabled → si el usuario está activo en el sistema
                true,                    // accountNonExpired → no gestionamos expiración de cuenta
                true,                    // credentialsNonExpired → no gestionamos expiración de credenciales
                true,                    // accountNonLocked → no gestionamos bloqueo de cuenta
                Collections.singleton(authority) // authorities → conjunto con un solo rol
        );
    }
}
